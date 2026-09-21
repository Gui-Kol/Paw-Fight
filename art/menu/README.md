# PawFight — botões e pausa

Arte criada com image_gen integrado em 2026-09-21, seguindo o HUD dourado, creme e azul-escuro. Exportações prontas nos caminhos consumidos pelo jogo.

## Entrega

- 18 PNGs: play, quit, save, resume, settings e plus, cada um com normal (1), hover (2) e pressionado (3).
- Painel fundoFinal.png (512×512) e pauseFundo-Sheet.png (3072×512), seis células de 512×512. A animação abre verticalmente e usa os mesmos seis frames para fechar em ordem inversa.
- Dimensões originais dos botões: 512×270; settings 512×300; plus 64×64.
- Cópias sincronizadas em assets/menu/button e assets/ui/buttons, e assets/menu/pause e assets/ui/pause.
- Nenhuma alteração em ações, áreas clicáveis, textos ou código Java. O botão SETTINGS mantém seu comportamento atual.

## Fontes e reprodução

source contém os PNGs gerados selecionados. pack.ps1 recorta as três células dos botões, ajusta para a grade de pixels, preserva alpha e exporta com vizinho mais próximo. O painel gerado fornece os seis frames por expansão vertical determinística. Os arquivos .aseprite antigos foram preservados como fontes legadas: eles não contêm a arte nova. Para regenerar a arte nova use source e pack.ps1.

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File art/menu/pack.ps1
powershell -NoProfile -ExecutionPolicy Bypass -File art/menu/pack.ps1 -VerifyOnly
```

Validação: dimensões, transparência e conteúdo, três estados distintos por botão, cópias idênticas, seis frames de abertura crescentes e último frame idêntico a fundoFinal.png. preview.png é uma composição dos PNGs exportados nas proporções do layout atual; não é uma captura do jogo. A revisão visual não inclui execução interativa do jogo.

## Prompts finais

### play

```text
Use case: stylized-concept. Asset type: production pixel-art button spritesheet for PawFight, matching its new gold/cream highlights and dark navy outlined HUD. Genuinely transparent PNG background. Exactly THREE copies of the SAME button arranged vertically in three equal-height cells, centers at y=16.667%,50%,83.333%; transparent gutters. Cell aspect ratio 512:270, overall canvas ratio 512:810. Each button occupies 90% cell width and 76% cell height. All three have EXACTLY same shape, dimensions, typography and horizontal alignment. Style: polished crisp low-resolution pixel art, chunky square pixels, warm amber-gold beveled frame, dark navy/slate interior, pale cream bold readable pixel lettering, subtle upper-left light, no gradients, no blur, no external glow. Frame has clipped stepped corners, small inset brass corner rivets, restrained fantasy dungeon look. Top NORMAL: muted gold rim and navy fill; middle HOVER: brighter golden rim and slightly lighter slate fill; bottom PRESSED: amber rim, darker interior, lettering lowered one logical pixel. No labels identifying states, no extra objects, no background, no watermark. Main text centered and large, one word per button, exact spelling. Text (verbatim) on every button: "PLAY". Use identical 5x7-style uppercase pixel lettering in all three states. Fit the complete word with generous horizontal margins.
```

### quit

```text
Use case: stylized-concept. Asset type: production pixel-art button spritesheet for PawFight, matching its new gold/cream highlights and dark navy outlined HUD. Genuinely transparent PNG background. Exactly THREE copies of the SAME button arranged vertically in three equal-height cells, centers at y=16.667%,50%,83.333%; transparent gutters. Cell aspect ratio 512:270, overall canvas ratio 512:810. Each button occupies 90% cell width and 76% cell height. All three have EXACTLY same shape, dimensions, typography and horizontal alignment. Style: polished crisp low-resolution pixel art, chunky square pixels, warm amber-gold beveled frame, dark navy/slate interior, pale cream bold readable pixel lettering, subtle upper-left light, no gradients, no blur, no external glow. Frame has clipped stepped corners, small inset brass corner rivets, restrained fantasy dungeon look. Top NORMAL: muted gold rim and navy fill; middle HOVER: brighter golden rim and slightly lighter slate fill; bottom PRESSED: amber rim, darker interior, lettering lowered one logical pixel. No labels identifying states, no extra objects, no background, no watermark. Main text centered and large, one word per button, exact spelling. Text (verbatim) on every button: "QUIT". Use identical 5x7-style uppercase pixel lettering in all three states. Fit the complete word with generous horizontal margins.
```

### save

```text
Use case: stylized-concept. Asset type: production pixel-art button spritesheet for PawFight, matching its new gold/cream highlights and dark navy outlined HUD. Genuinely transparent PNG background. Exactly THREE copies of the SAME button arranged vertically in three equal-height cells, centers at y=16.667%,50%,83.333%; transparent gutters. Cell aspect ratio 512:270, overall canvas ratio 512:810. Each button occupies 90% cell width and 76% cell height. All three have EXACTLY same shape, dimensions, typography and horizontal alignment. Style: polished crisp low-resolution pixel art, chunky square pixels, warm amber-gold beveled frame, dark navy/slate interior, pale cream bold readable pixel lettering, subtle upper-left light, no gradients, no blur, no external glow. Frame has clipped stepped corners, small inset brass corner rivets, restrained fantasy dungeon look. Top NORMAL: muted gold rim and navy fill; middle HOVER: brighter golden rim and slightly lighter slate fill; bottom PRESSED: amber rim, darker interior, lettering lowered one logical pixel. No labels identifying states, no extra objects, no background, no watermark. Main text centered and large, one word per button, exact spelling. Text (verbatim) on every button: "SAVE". Use identical 5x7-style uppercase pixel lettering in all three states. Fit the complete word with generous horizontal margins.
```

### resume

```text
Use case: stylized-concept. Asset type: production pixel-art button spritesheet for PawFight, matching its new gold/cream highlights and dark navy outlined HUD. Genuinely transparent PNG background. Exactly THREE copies of the SAME button arranged vertically in three equal-height cells, centers at y=16.667%,50%,83.333%; transparent gutters. Cell aspect ratio 512:270, overall canvas ratio 512:810. Each button occupies 90% cell width and 76% cell height. All three have EXACTLY same shape, dimensions, typography and horizontal alignment. Style: polished crisp low-resolution pixel art, chunky square pixels, warm amber-gold beveled frame, dark navy/slate interior, pale cream bold readable pixel lettering, subtle upper-left light, no gradients, no blur, no external glow. Frame has clipped stepped corners, small inset brass corner rivets, restrained fantasy dungeon look. Top NORMAL: muted gold rim and navy fill; middle HOVER: brighter golden rim and slightly lighter slate fill; bottom PRESSED: amber rim, darker interior, lettering lowered one logical pixel. No labels identifying states, no extra objects, no background, no watermark. Main text centered and large, one word per button, exact spelling. Text (verbatim) on every button: "RESUME". Use identical 5x7-style uppercase pixel lettering in all three states. Fit the complete word with generous horizontal margins.
```

### settings

```text
Use case: stylized-concept. Asset type: production pixel-art button spritesheet for PawFight, matching its new gold/cream highlights and dark navy outlined HUD. Genuinely transparent PNG background. Exactly THREE copies of the SAME button arranged vertically in three equal-height cells, centers at y=16.667%,50%,83.333%; transparent gutters. Cell aspect ratio 512:270, overall canvas ratio 512:810. Each button occupies 90% cell width and 76% cell height. All three have EXACTLY same shape, dimensions, typography and horizontal alignment. Style: polished crisp low-resolution pixel art, chunky square pixels, warm amber-gold beveled frame, dark navy/slate interior, pale cream bold readable pixel lettering, subtle upper-left light, no gradients, no blur, no external glow. Frame has clipped stepped corners, small inset brass corner rivets, restrained fantasy dungeon look. Top NORMAL: muted gold rim and navy fill; middle HOVER: brighter golden rim and slightly lighter slate fill; bottom PRESSED: amber rim, darker interior, lettering lowered one logical pixel. No labels identifying states, no extra objects, no background, no watermark. Main text centered and large, one word per button, exact spelling. Text (verbatim) on every button: "SETTINGS". Use identical 5x7-style uppercase pixel lettering in all three states. Fit the complete word with generous horizontal margins.
```

### plus

```text
Use case: stylized-concept. Asset type: production pixel-art button spritesheet for PawFight, matching its new gold/cream highlights and dark navy outlined HUD. Genuinely transparent PNG background. Exactly THREE copies of the SAME button arranged vertically in three equal-height cells, centers at y=16.667%,50%,83.333%; transparent gutters. Cell aspect ratio 512:270, overall canvas ratio 512:810. Each button occupies 90% cell width and 76% cell height. All three have EXACTLY same shape, dimensions, typography and horizontal alignment. Style: polished crisp low-resolution pixel art, chunky square pixels, warm amber-gold beveled frame, dark navy/slate interior, pale cream bold readable pixel lettering, subtle upper-left light, no gradients, no blur, no external glow. Frame has clipped stepped corners, small inset brass corner rivets, restrained fantasy dungeon look. Top NORMAL: muted gold rim and navy fill; middle HOVER: brighter golden rim and slightly lighter slate fill; bottom PRESSED: amber rim, darker interior, lettering lowered one logical pixel. No labels identifying states, no extra objects, no background, no watermark. Main text centered and large, one word per button, exact spelling. Override dimensions: each cell square 64:64; entire sheet 1:3 aspect ratio. Text: a single centered cream '+' symbol on each of the three square buttons.
```

### Painel inicial

```text
Use case: stylized-concept. Asset type: production pause menu panel background for PawFight pixel-art dungeon game. One large SQUARE panel, front orthographic view, centered, occupying 94% canvas width and height, genuine transparent alpha surrounding the panel. Pixel art with crisp stepped corners, dark navy outer outline, warm amber/gold double beveled rim with cream highlights at upper left and amber shadows bottom right, small brass corner rivets. Dark matte slate-navy interior, almost uniform color so overlaid menu buttons are clearly visible. Very restrained tiny paw-print engraving centered at bottom rim only. Keep the central 80% of the panel completely empty and undecorated. No text, no buttons, no title, no watermark, no separate objects, no external glow or shadow. Authentic low resolution pixel-art designed on a 128x128 grid and enlarged with nearest-neighbor. Restrained colors matching gold/cream PawFight HUD coin with navy outline. Square PNG.
```

### Correção do preenchimento do painel

```text
Use case: precise-object-edit. Image 1 is the edit target: a PawFight pause menu frame. Change ONLY the interior fill: replace the entire region inside the golden rim with ONE uniform solid opaque slate navy color #182337. Remove the central black rectangle, all gradients, vignettes, interior patches, lighting haze and holes. Interior must be completely flat and opaque edge-to-edge up to the gold rim. Keep the exact gold pixel-art frame, four rivets, bottom paw emblem, square proportions, size and transparency outside the panel unchanged. No text, no buttons. Transparent alpha outside panel only. Sharp pixel art. This is a final production UI texture.
```
