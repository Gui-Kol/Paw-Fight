# PawFight — HUD atualizado

Produzido com image_gen integrado em 2026-09-21. Os desenhos vieram da geração de imagens; o empacotamento local apenas recorta e reamostra por vizinho mais próximo, preservando o canal alpha.

## Arquivos de produção

Oito PNGs em assets/Hud, espelhados byte a byte em assets/ui/hud: coin, coracao, coracao1, coracoes, musculo, nuvemChao, raio e requa. Dimensões e nomes originais preservados. Os arquivos Java não precisaram de alteração.

coracao.png mantém 97×16: cinco células de 19×16 nos primeiros 95 pixels, com dois pixels finais transparentes, conforme a divisão inteira de MotorAnimacao. Ordem: cheio, 75%, 50%, 25%, vazio. coracao1.png é exatamente a primeira célula. coracoes.png é o atlas auxiliar de 196×71; não há referências a ele no código Java atual. A arte desse atlas foi redesenhada e não deve ser tratada como um contrato de coordenadas para usos externos.

raio e nuvemChao também são usados por TiroSolar e TiroGelo. Suas novas artes aparecerão nesses projéteis, mantendo os caminhos e dimensões esperados.

## Revisão e reprodução

preview.png apresenta as exportações finais em fundo escuro e claro. source contém as imagens geradas selecionadas. Execute no Windows:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File art/hud/pack.ps1
powershell -NoProfile -ExecutionPolicy Bypass -File art/hud/pack.ps1 -VerifyOnly
```

O script valida dimensões, transparência, presença de conteúdo e igualdade das cópias. A revisão foi feita nos arquivos exportados, sem execução interativa do jogo.

## Prompts finais

### coin.png

```text
Use case: stylized-concept. Asset type: single production HUD icon for PawFight, a pixel-art animal dungeon game. Generate a single gold coin with an embossed paw print, front view, circular stepped pixel outline. True transparent background with alpha, no backdrop, no floor, no text, no mockup, no extra objects. Crisp authentic low-resolution pixel art, designed on a 32x32 pixel grid and enlarged with nearest-neighbor blocks. Dark navy-brown outline 1 logical pixel, warm gold midtone, amber shadow lower right, pale cream highlight upper left, restrained 6-color palette, no blur, no gradients, no antialiasing. Centered with 2 logical pixels transparent margin, strong silhouette legible at 32 pixels. Square PNG.
```

### raio.png

```text
Use case: stylized-concept. Asset type: production HUD icon for PawFight, pixel-art animal dungeon game. True transparent PNG background with alpha. Authentic crisp 32x32 logical pixel art enlarged in uniform nearest-neighbor blocks; 1 logical pixel dark navy-brown outline, small pale cream highlight upper left, restrained 6-color palette, hard stepped edges, no blur or gradients or antialiasing. One isolated icon centered on square canvas with generous transparent margins. No text, no other objects, no badges, no mockup, no drop shadows outside sprite. Single golden lightning bolt, amber lower right shading, lemon-yellow fill and cream highlight, confident jagged silhouette. Occupy central 50 percent canvas width and 66 percent height, matching an energy/speed icon.
```

### musculo.png

```text
Use case: stylized-concept. Asset type: production HUD icon for PawFight, pixel-art animal dungeon game. True transparent PNG background with alpha. Authentic crisp 32x32 logical pixel art enlarged in uniform nearest-neighbor blocks; 1 logical pixel dark navy-brown outline, small pale cream highlight upper left, restrained 6-color palette, hard stepped edges, no blur or gradients or antialiasing. One isolated icon centered on square canvas with generous transparent margins. No text, no other objects, no badges, no mockup, no drop shadows outside sprite. Single flexed biceps arm strength icon, closed fist at top, elbow at bottom right, upper arm flexed to left, warm tan with peach highlights and terracotta shadows. Occupy central 62 percent canvas height and width. Strong simple readable silhouette.
```

### requa.png

```text
Use case: stylized-concept. Asset type: production HUD icon for PawFight, pixel-art animal dungeon game. True transparent PNG background with alpha. Authentic crisp 32x32 logical pixel art enlarged in uniform nearest-neighbor blocks; 1 logical pixel dark navy-brown outline, small pale cream highlight upper left, restrained 6-color palette, hard stepped edges, no blur or gradients or antialiasing. One isolated icon centered on square canvas with generous transparent margins. No text, no other objects, no badges, no mockup, no drop shadows outside sprite. Single horizontal steel-blue measuring ruler, ivory highlights and dark navy tick marks, no numbers or letters. Straight horizontal, width 80 percent of canvas and height 18 percent, centered. Clear alternating long and short measurement ticks.
```

### nuvemChao.png

```text
Use case: stylized-concept. Asset type: production HUD icon for PawFight, pixel-art animal dungeon game. True transparent PNG background with alpha. Authentic crisp 32x32 logical pixel art enlarged in uniform nearest-neighbor blocks; 1 logical pixel dark navy-brown outline, small pale cream highlight upper left, restrained 6-color palette, hard stepped edges, no blur or gradients or antialiasing. One isolated icon centered on square canvas with generous transparent margins. No text, no other objects, no badges, no mockup, no drop shadows outside sprite. Single horizontal low blue icy dust cloud with curled puffs, pale cyan highlights and medium blue shaded underside, dark navy outline. Wide silhouette, approximately 3.67 times wider than tall, centered with transparent surrounding space. Must work as a ground cloud and ice projectile, no snowflakes outside silhouette.
```

### health.png → coracao.png e coracao1.png

Referência de edição: assets/Hud/coracao.png original.

```text
Use case: style-transfer. Edit target: PawFight health HUD spritesheet. Redesign this ONE sprite strip of exactly FIVE hearts preserving exact left-to-right health semantics: 100% full ruby red, 75% red fill, 50% red fill, 25% red fill, 0% empty dark blue-grey interior. Exactly five identical-size heart silhouettes, evenly spaced on one horizontal row, centers at 10%,30%,50%,70%,90% canvas width, no extra row. All five share identical outer border and position inside their cell. Crisp low resolution pixel-art designed for 19x16 logical-pixel cells, enlarged for delivery. Dark navy outline, light slate inner rim for readable empty shell, ruby red/coral health fill, tiny cream-pink highlight on filled portion, deep burgundy shadows. Empty area fills from right toward left as life decreases. Transparent PNG alpha background, no text, no labels, no checkerboard, no shadows outside hearts. Each heart fits in 80% of its equal-width cell, with a clear transparent gap. Entire output wide ratio matching reference strip 97:16.
```

### atlas.png → coracoes.png

Referência de edição: assets/Hud/coracoes.png original.

```text
Use case: style-transfer. Edit target: this PawFight health icon atlas. Redesign the whole atlas in cohesive crisp pixel art: dark navy outlines, ruby/coral red fills, pale pink highlights upper left, slate grey empty hearts, mint green healing crosses. Preserve all original symbols, exact arrangement and approximate sizes: left half rows of five health hearts from full to empty, right half decorative heart variants and lower medical/healing symbols. Keep each cell isolated with transparent gaps. Preserve image aspect 196:71. Transparent background with real alpha, no backdrop, no text added. Use chunky clean pixel art, limited color palette, no blur, no smooth vector curves. This is a production game spritesheet, not a presentation.
```

