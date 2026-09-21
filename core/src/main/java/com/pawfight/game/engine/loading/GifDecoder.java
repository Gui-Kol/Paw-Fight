package com.pawfight.game.engine.loading;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class GifDecoder {

    private static final int MAX_STACK_SIZE = 4096;

    private InputStream in;
    private boolean eof;

    private int width;
    private int height;
    private int[] gct;       // global color table
    private int[] act;       // active color table
    private int bgIndex;
    private int bgColor;

    private boolean interlace;
    private int ix, iy, iw, ih;

    private int lastDispose;
    private int dispose;
    private boolean transparency;
    private int transIndex;
    private int delay;

    private int[] dest;
    private int[] prev;
    private final byte[] block = new byte[256];
    private int blockSize;

    private short[] prefix;
    private byte[] suffix;
    private byte[] pixelStack;
    private byte[] pixels;

    private final List<GifFrame> frames = new ArrayList<>();

    public static GifAnimation loadGifAnimation(FileHandle gifFile) {
        GifDecoder decoder = new GifDecoder();
        decoder.read(gifFile);

        if (decoder.frames.isEmpty()) {
            throw new RuntimeException("Nenhum frame decodificado do GIF: " + gifFile.path()
                + " (w=" + decoder.width + ", h=" + decoder.height + ")");
        }

        Gdx.app.log("GifDecoder", "GIF decodificado: " + gifFile.name()
            + " — " + decoder.frames.size() + " frames, "
            + decoder.width + "x" + decoder.height);

        Array<TextureRegion> regions = new Array<>();
        Array<Texture> textures = new Array<>();

        float frameDuration = decoder.frames.get(0).delay / 1000f;
        if (frameDuration <= 0) frameDuration = 0.1f;

        for (GifFrame gf : decoder.frames) {
            Pixmap pixmap = new Pixmap(decoder.width, decoder.height, Pixmap.Format.RGBA8888);
            ByteBuffer buf = pixmap.getPixels();
            // Converte cada int ARGB em bytes RGBA para o pixmap
            for (int argb : gf.pixels) {
                buf.put((byte) ((argb >> 16) & 0xFF));
                buf.put((byte) ((argb >> 8) & 0xFF));
                buf.put((byte) (argb & 0xFF));
                buf.put((byte) ((argb >> 24) & 0xFF));
            }
            buf.flip();

            Texture tex = new Texture(pixmap);
            tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            textures.add(tex);
            regions.add(new TextureRegion(tex));
            pixmap.dispose();
        }

        Animation<TextureRegion> anim = new Animation<>(frameDuration, regions, Animation.PlayMode.LOOP);
        return new GifAnimation(anim, textures);
    }

    private void read(FileHandle file) {
        try {
            in = new BufferedInputStream(file.read());
            eof = false;
            readHeader();
            if (eof) {
                Gdx.app.error("GifDecoder", "Header inválido / EOF prematuro: " + file.name());
                return;
            }
            readContents();
            Gdx.app.log("GifDecoder", "Leitura concluída: " + frames.size() + " frames de " + file.name());
        } catch (Exception e) {
            Gdx.app.error("GifDecoder", "Exceção ao ler GIF: " + file.name(), e);
        } finally {
            try { if (in != null) in.close(); } catch (Exception ignored) {}
        }
    }

    private void readHeader() {
        byte[] header = new byte[6];
        for (int i = 0; i < 6; i++) header[i] = (byte) readByte();
        if (eof) return;

        String sig = new String(header);
        if (!sig.startsWith("GIF")) {
            Gdx.app.error("GifDecoder", "Assinatura inválida: [" + sig + "]");
            eof = true;
            return;
        }

        width = readShort();
        height = readShort();
        if (width <= 0 || height <= 0) {
            Gdx.app.error("GifDecoder", "Dimensões inválidas: " + width + "x" + height);
            eof = true;
            return;
        }

        int packed = readByte();
        boolean gctFlag = (packed & 0x80) != 0;
        int gctSize = 2 << (packed & 7);

        bgIndex = readByte();
        readByte(); // pixel aspect ratio

        if (gctFlag) {
            gct = readColorTable(gctSize);
            if (bgIndex < gct.length) {
                bgColor = gct[bgIndex];
            }
        }

        dest = new int[width * height];
    }

    private void readContents() {
        int safetyCounter = 0; // proteção contra loop infinito

        while (!eof) {
            int code = readByte();
            if (eof) break;

            if (code == 0x3B) { // GIF Trailer
                break;
            }

            if (code == 0x2C) { // Image Descriptor
                readImage();
                safetyCounter = 0; // reset — progresso real
                continue;
            }

            if (code == 0x21) { // Extension
                int extCode = readByte();
                if (eof) break;
                if (extCode == 0xF9) {
                    readGraphicControlExt();
                } else {
                    skipSubBlocks();
                }
                safetyCounter = 0;
                continue;
            }

            // Byte desconhecido / noop / padding
            safetyCounter++;
            if (safetyCounter > 1000) {
                Gdx.app.error("GifDecoder", "Muitos bytes desconhecidos seguidos — abortando leitura");
                break;
            }
        }
    }

    private void readGraphicControlExt() {
        readByte(); // block size (sempre 4)
        int packed = readByte();
        dispose = (packed & 0x1C) >> 2;
        transparency = (packed & 1) != 0;
        delay = readShort() * 10; // centésimos de segundo → ms
        transIndex = readByte();
        readByte(); // block terminator
    }

    private void readImage() {
        ix = readShort();
        iy = readShort();
        iw = readShort();
        ih = readShort();
        if (eof || iw <= 0 || ih <= 0) return;

        int packed = readByte();
        boolean lctFlag = (packed & 0x80) != 0;
        interlace = (packed & 0x40) != 0;
        int lctSize = 2 << (packed & 7);

        if (lctFlag) {
            act = readColorTable(lctSize);
        } else {
            act = gct;
        }

        if (act == null) {
            Gdx.app.error("GifDecoder", "Tabela de cores nula — criando grayscale fallback");
            act = new int[256];
            for (int i = 0; i < 256; i++) {
                act[i] = 0xFF000000 | (i << 16) | (i << 8) | i;
            }
        }

        int saveBg = 0;
        if (transparency && transIndex >= 0 && transIndex < act.length) {
            saveBg = act[transIndex];
            act[transIndex] = 0;
        }

        // Disposal do frame anterior
        if (lastDispose == 2) { // RESTORE_BG
            for (int i = 0; i < dest.length; i++) {
                dest[i] = bgColor;
            }
        } else if (lastDispose == 3 && prev != null) { // RESTORE_PREV
            System.arraycopy(prev, 0, dest, 0, dest.length);
        }

        // Backup para RESTORE_PREV
        if (dispose == 3) {
            prev = dest.clone();
        }

        int minCodeSize = readByte();
        if (!eof) {
            decodeLZW(minCodeSize);
        }

        transferPixels();

        GifFrame frame = new GifFrame();
        frame.pixels = dest.clone();
        frame.delay = Math.max(delay, 20);
        frames.add(frame);

        // Restaurar transparência
        if (transparency && transIndex >= 0 && transIndex < act.length) {
            act[transIndex] = saveBg;
        }

        lastDispose = dispose;
        transparency = false;
        dispose = 0;
    }

    private void transferPixels() {
        if (pixels == null) return;

        int pass = 1, inc = 8, iline = 0;

        for (int i = 0; i < ih; i++) {
            int line = i;
            if (interlace) {
                if (iline >= ih) {
                    pass++;
                    switch (pass) {
                        case 2: iline = 4; break;
                        case 3: iline = 2; inc = 4; break;
                        case 4: iline = 1; inc = 2; break;
                    }
                }
                line = iline;
                iline += inc;
            }

            int dy = line + iy;
            if (dy < 0 || dy >= height) continue;

            int srcOffset = i * iw;

            for (int k = 0; k < iw; k++) {
                int dx = ix + k;
                if (dx < 0 || dx >= width) continue;

                int pidx = srcOffset + k;
                if (pidx >= pixels.length) continue;

                int colorIdx = pixels[pidx] & 0xFF;
                if (colorIdx >= act.length) continue;

                int color = act[colorIdx];
                if (color != 0) {
                    dest[dy * width + dx] = color;
                }
            }
        }
    }

    private void decodeLZW(int minCodeSize) {
        if (minCodeSize < 2 || minCodeSize > 12) {
            Gdx.app.error("GifDecoder", "minCodeSize inválido: " + minCodeSize + " — pulando frame");
            skipSubBlocks();
            pixels = new byte[iw * ih];
            return;
        }

        int npix = iw * ih;
        pixels = new byte[npix];

        if (prefix == null) prefix = new short[MAX_STACK_SIZE];
        if (suffix == null) suffix = new byte[MAX_STACK_SIZE];
        if (pixelStack == null) pixelStack = new byte[MAX_STACK_SIZE + 1];

        int clear = 1 << minCodeSize;
        int endOfInfo = clear + 1;
        int available = clear + 2;
        int oldCode = -1;
        int codeSize = minCodeSize + 1;
        int codeMask = (1 << codeSize) - 1;

        for (int code = 0; code < clear; code++) {
            prefix[code] = 0;
            suffix[code] = (byte) code;
        }

        int datum = 0, bits = 0, first = 0, top = 0, pi = 0, bi = 0;
        int count = 0;

        for (int i = 0; i < npix && !eof; ) {
            if (top == 0) {
                if (bits < codeSize) {
                    if (count == 0) {
                        count = readBlock();
                        if (count <= 0) break;
                        bi = 0;
                    }
                    datum += (block[bi] & 0xFF) << bits;
                    bits += 8;
                    bi++;
                    count--;
                    continue;
                }

                int code = datum & codeMask;
                datum >>= codeSize;
                bits -= codeSize;

                if (code == endOfInfo) break;

                if (code == clear) {
                    codeSize = minCodeSize + 1;
                    codeMask = (1 << codeSize) - 1;
                    available = clear + 2;
                    oldCode = -1;
                    continue;
                }

                if (oldCode == -1) {
                    if (code < clear) {
                        pixelStack[top++] = suffix[code];
                        oldCode = code;
                        first = code;
                    }
                    continue;
                }

                int inCode = code;
                if (code >= available) {
                    pixelStack[top++] = (byte) first;
                    code = oldCode;
                }

                while (code >= clear && top < MAX_STACK_SIZE) {
                    pixelStack[top++] = suffix[code];
                    code = prefix[code];
                }
                first = suffix[code] & 0xFF;

                pixelStack[top++] = (byte) first;

                if (available < MAX_STACK_SIZE) {
                    prefix[available] = (short) oldCode;
                    suffix[available] = (byte) first;
                    available++;

                    if ((available & codeMask) == 0 && available < MAX_STACK_SIZE) {
                        codeSize++;
                        codeMask = (1 << codeSize) - 1;
                    }
                }
                oldCode = inCode;
            }

            top--;
            pixels[pi++] = pixelStack[top];
            i++;
        }

        for (int i = pi; i < npix; i++) {
            pixels[i] = 0;
        }

        // Consome sub-blocos restantes do frame — essencial para não desincronizar o stream
        skipSubBlocks();
    }

    // Lê um byte; retorna 0 e seta eof = true no fim do stream
    private int readByte() {
        try {
            int b = in.read();
            if (b < 0) {
                eof = true;
                return 0;
            }
            return b;
        } catch (Exception e) {
            eof = true;
            return 0;
        }
    }

    private int readShort() {
        int lo = readByte();
        int hi = readByte();
        return lo | (hi << 8);
    }

    // Lê um sub-bloco GIF (1 byte de tamanho + dados) e retorna quantos bytes foram lidos
    private int readBlock() {
        blockSize = readByte();
        int n = 0;
        if (blockSize > 0) {
            try {
                while (n < blockSize) {
                    int r = in.read(block, n, blockSize - n);
                    if (r < 0) { eof = true; break; }
                    n += r;
                }
            } catch (Exception e) {
                eof = true;
            }
        }
        return n;
    }

    private int[] readColorTable(int ncolors) {
        int nbytes = 3 * ncolors;
        byte[] raw = new byte[nbytes];
        try {
            int n = 0;
            while (n < nbytes) {
                int r = in.read(raw, n, nbytes - n);
                if (r < 0) { eof = true; break; }
                n += r;
            }
        } catch (Exception e) {
            eof = true;
        }

        int[] tab = new int[ncolors];
        int j = 0;
        for (int i = 0; i < ncolors && j + 2 < nbytes; i++) {
            int r = raw[j++] & 0xFF;
            int g = raw[j++] & 0xFF;
            int b = raw[j++] & 0xFF;
            tab[i] = 0xFF000000 | (r << 16) | (g << 8) | b;
        }
        return tab;
    }

    // Pula sub-blocos até o terminador (tamanho 0), byte a byte (sem in.skip) por confiabilidade
    private void skipSubBlocks() {
        while (!eof) {
            int size = readByte();
            if (size <= 0 || eof) break;
            for (int i = 0; i < size && !eof; i++) {
                readByte();
            }
        }
    }

    private static class GifFrame {
        int[] pixels;
        int delay;
    }

    public static class GifAnimation {
        public final Animation<TextureRegion> animation;
        private final Array<Texture> textures;

        public GifAnimation(Animation<TextureRegion> animation, Array<Texture> textures) {
            this.animation = animation;
            this.textures = textures;
        }

        public void dispose() {
            for (Texture t : textures) {
                t.dispose();
            }
        }
    }
}
