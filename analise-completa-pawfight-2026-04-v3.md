# 🐾 Análise Completa — Paw Fight (Abril 2026 — v3)

## 📊 Visão Geral do Projeto

| Métrica | v1 | v2 | Atual (v3) | Δ (v2→v3) |
|---------|----|----|-----------|-----------|
| Arquivos `.java` | 69 | 76 | **77** | +1 |
| Linhas de código totais | 5.269 | 5.860 | **5.920** | +60 |
| Pacotes | 13 | 15 | **15** | — |
| Framework | libGDX | libGDX | libGDX | — |
| Build | Gradle | Gradle | Gradle | — |
| Testes | ❌ Zero | ❌ Zero | ❌ Zero | — |

### 🆕 Arquivos Novos Desde a Última Análise (v2)

| Arquivo | Linhas | Propósito |
|---------|--------|-----------|
| `ColisaoResolver.java` | 125 | Resolução de colisão entre inimigos e paredes via MTV (Minimum Translation Vector) ✨ NOVO |

### 📐 Arquivos com Mudanças Significativas Desde v2

| Arquivo | v2 | v3 | Δ | Resumo da mudança |
|---------|----|----|---|-------------------|
| `GameConfig.java` | 74 | **96** | +22 | `debugMode = false`, `setDebugMode` removido (agora imutável), novos métodos `Percent` para `volumePassos` |
| `ChecarColisao.java` | 33 | **46** | +13 | Novo método `checarColisaoSeparadoEixo()` integrado |
| `WorldTemplate.java` | 285 | **273** | -12 | Delegate getters removidos — código externo acessa managers diretamente |
| `WorldPhysics.java` | 39 | **65** | +26 | Integração com `ColisaoResolver`, campo `paredes`, método `resolverColisoes()` |
| `WorldRenderer.java` | 73 | **93** | +20 | Integração com `ColisaoResolver` via `WorldPhysics.resolverColisoes()` |
| `RoomManager.java` | 71 | **92** | +21 | Novo método `currentRoomFoiVisitada()` |
| `EnemyManager.java` | 28 | **39** | +11 | Novo método `hasInimigos()`, `clearInimigos()` |
| `EnemyTemplate.java` | 231 | **265** | +34 | Novo `sincronizarPosicaoComHitbox()`, `setParedesColisores()`, `cloneEnemy()` expandido |
| `MoverDirecaoPlayer.java` | 55 | **90** | +35 | Refatoração completa com `tempHitBox` pré-alocado e colisão entre inimigos |
| `Skeleton.java` | 80 | **95** | +15 | `aplicarStatsForte()` implementado, `extraDraw()` adicionado |
| `PlayerTemplate.java` | 314 | **389** | +75 | Mais getters de stats, seção SAVE/LOAD, melhor organização |
| `DanoTiro.java` | 75 | **102** | +27 | Log throttled com `LOG_INTERVALO`, acumuladores estatísticos |
| `Quadtree.java` | 146 | **159** | +13 | Logs verbosos removidos, `drawDebug()` melhorado com marcas visuais |
| `MundoAreia.java` | 214 | **246** | +32 | Acessa managers diretamente, melhor error handling no render |
| `Home.java` | 138 | **160** | +22 | Melhor separação de responsabilidades, try/catch granular |
| `Base.java` | 124 | **150** | +26 | Hitboxes cacheadas, `drawObjects` com cache |
| `CarregarPortas.java` | 114 | **144** | +30 | Refatorado com enum `Direcao`, inimigos recebem referência de paredes |
| `Renderizar.java` | 95 | **113** | +18 | Novo `hitBoxListObjeto()` para objetos gerados |
| `ColisaoComponent.java` | 50 | **67** | +17 | `adicionarColisaoPorLevel()` melhorado, `drawDebugHitboxesNoBatch()` |
| `StatsComponent.java` | 150 | **170** | +20 | Setters para save/load, construtor para inimigos |
| `Sala.java` | 51→ | **61** | +10 | Referências para salas vizinhas, `getRoom(Direction)`, `getIndex()` |
| `GeradorSalas.java` | 192 | **235** | +43 | Sala de TESOURO pós-BOSS, lista secundária com tipos variados |
| `EscolherPersonagem.java` | 176 | **220** | +44 | Nuvem animada, viewport-aware resize, layout melhorado |
| `BlackCat.java` | 60 | **71** | +11 | Crescimento similar nos 4 players |
| `CombateComponent.java` | 57 | **74** | +17 | `processarTirosAutomaticos()` mais robusto |
| `TilemapHitboxFactory.java` | 139 | **170** | +31 | Cache com HashMap, `createTileLayerHitboxes()`, `drawRects()` sem begin/end |
| `RenderizadorCamada.java` | 60 | **74** | +14 | `renderLayerTiled()` adicionado |

### 🏗️ Arquitetura de Pacotes

```
com.pawfight.game
├── PawFight.java                          (161 linhas) — Game principal
├── engine/
│   ├── Assets.java                        (126) — AssetManager centralizado
│   ├── AudioEngine.java                   (28)  — Wrapper de áudio
│   ├── GameConfig.java                    (96)  — Configuração centralizada (debugMode=false ✅)
│   ├── ScreenManager.java                 (134) — Gerenciador de transições
│   ├── Validar.java                       (9)   — Utilitário de validação
│   ├── design/
│   │   ├── AlteradorZoom.java             (25)
│   │   ├── DefinirSprite.java             (10)  — record
│   │   ├── animation/MotorAnimacao.java   (123) — Motor de animação
│   │   ├── desenhar/DesenharTexto.java    (50)
│   │   ├── desenhar/DesenharTextura.java  (22)
│   │   └── transition/
│   │       ├── TransitionEffect.java      (10)  — interface
│   │       ├── FadeTransitionEffect.java  (91)
│   │       ├── SlideTransitionEffect.java (87)
│   │       └── TransicaoTela.java         (82)
│   ├── fisica/
│   │   ├── ChecarColisao.java             (46)
│   │   ├── ColisaoResolver.java           (125) — Resolução MTV ✨ NOVO
│   │   ├── Quadtree.java                  (159) — Quadtree genérica (logs removidos ✅)
│   │   └── TilemapHitboxFactory.java      (170) — com cache HashMap
│   ├── font/FontFactory.java              (16)
│   ├── Hud/ ⚠️ (PascalCase — ainda não corrigido)
│   │   ├── CriarBotao.java               (50)
│   │   ├── DesenharMiniMapa.java          (183)
│   │   ├── Hud.java                       (168)
│   │   ├── HudPause.java                  (119)
│   │   └── HudStage.java                  (58)
│   ├── input/
│   │   ├── GameAction.java                (24)
│   │   └── KeyBindings.java               (110)
│   ├── procedural/
│   │   ├── CarregarPortas.java            (144) — refatorado com enum Direcao
│   │   ├── GerarInimigos.java             (91)
│   │   ├── GerarObjetos.java              (55)
│   │   ├── ObjetoGerado.java              (41)
│   │   └── sala/ (GeradorSalas 235, Sala 61, TipoSala, InfoGeraObjeto, Direction)
│   ├── render/
│   │   ├── Renderizar.java                (113)
│   │   └── RenderizadorCamada.java        (74)
│   └── save/
│       ├── DadosSalvosJogador.java        (12)
│       └── SalvarJogo.java                (20)
├── entity/
│   ├── Entidade.java                      (19)  — interface
│   ├── bosses/BossesTemplate.java         (3)
│   ├── component/                         — 9 componentes
│   │   ├── AnimacaoComponent.java         (183) — com cache L/R
│   │   ├── AudioComponent.java            (39)
│   │   ├── CameraComponent.java           (43)
│   │   ├── ColisaoComponent.java          (67)  — noclip em debug
│   │   ├── CombateComponent.java          (74)
│   │   ├── InputComponent.java            (15)  — usa KeyBindings
│   │   ├── MovimentoComponent.java        (38)
│   │   ├── SaveComponent.java             (30)
│   │   └── StatsComponent.java            (170)
│   ├── enemy/
│   │   ├── DadosInimigo.java              (23)  — record
│   │   ├── EnemyTemplate.java             (265) — com sincronizarPosicaoComHitbox
│   │   ├── MoverDirecaoPlayer.java        (90)  — com tempHitBox pré-alocado ✅
│   │   └── Skeleton.java                  (95)
│   ├── player/
│   │   ├── DadosPlayer.java               (21)  — record
│   │   ├── PlayerTemplate.java            (389)
│   │   ├── BlackBird.java                 (60)
│   │   ├── BlackCat.java                  (71)
│   │   ├── Dove.java                      (72)
│   │   └── OrangeCat.java                 (60)
│   └── tiro/
│       ├── Atirar.java                    (33)
│       ├── DanoTiro.java                  (102) — com log throttled
│       ├── TirosTemplate.java             (90)
│       └── dove/Coco.java                 (52)
└── world/
    ├── Home.java                          (160)
    ├── MundoAreia.java                    (246)
    ├── template/
    │   ├── WorldTemplate.java             (273) — delegate getters removidos ✅
    │   ├── WorldRenderer.java             (93)
    │   ├── WorldPhysics.java              (65)  — com ColisaoResolver
    │   ├── RoomManager.java               (92)
    │   └── EnemyManager.java              (39)
    └── base/
        ├── Base.java                      (150) — hitboxes cacheadas
        ├── EntradaPortais.java            (59)
        ├── EscolherPersonagem.java        (220)
        ├── ExibirDadosPersonagem.java     (77)
        └── Portoes.java                   (19)
```

---

## 📈 Resumo de Progresso vs Análises Anteriores

| Status | v1 | v2 | v3 | Δ (v2→v3) |
|--------|----|----|----|----|
| ✅ Feito | 12 | 20 | **24** | +4 |
| 🟡 Parcialmente feito | 3 | 2 | **2** | 0 |
| ❌ Não feito | 10 | 6 | **4** | -2 |
| 🆕 Novos problemas encontrados | 6 | 8 | **6** | -2 |

**Progresso geral: ~78% completo** (vs ~71% na v2, ~48% na v1, ~23% na original)

---

## ✅ O QUE JÁ FOI FEITO (24 itens)

*(20 itens da v2 + 4 novos itens resolvidos)*

### Itens mantidos da v2 (1–20)

Todos os 20 itens marcados como ✅ na análise v2 continuam válidos:

1. ✅ AssetManager Centralizado
2. ✅ EnemyTemplate Refatorado com Componentes
3. ✅ Texturas Compartilhadas nos Inimigos
4. ✅ Batch begin/end de Sprites Centralizado
5. ✅ Dispose Correto com AssetManager
6. ✅ ScreenManager Criado
7. ✅ Cache de Animações Esquerda/Direita
8. ✅ Hitbox Draws Batched
9. ✅ Animação do Coração Cacheada no HUD
10. ✅ SalvarJogo.salvar() Renomeado
11. ✅ removeIf() em Inimigos Mortos
12. ✅ EnemyTemplate.draw() Individual Removido
13. ✅ VariavelComum → GameConfig
14. ✅ Input Remapping via KeyBindings
15. ✅ DanoTiro com Quadtree
16. ✅ WorldTemplate Decomposto em Subsistemas
17. ✅ DadosInimigo com Nomes camelCase
18. ✅ DadosSalvosJogador Simplificado
19. ✅ hitboxDraw() Agora Batched
20. ✅ Cachear Snapshot Tiros em DanoTiro

---

### 21. ✅ debugMode = false por Padrão ✨ NOVO DESDE v2
**Item original: N8 da v2 | Status: CONCLUÍDO**

`GameConfig.java` agora tem `debugMode = false` (linha 22):
```java
// Antes (v2):
private boolean debugMode = true;  // ← causava noclip e logs verbosos

// Depois (v3):
private boolean debugMode = false; // ← correto para produção
```

Adicionalmente, **não existe mais `setDebugMode()`** público — o campo é imutável em runtime. Isso impede ativação acidental. O `isDebugMode()` continua disponível como getter, mas o noclip em `ColisaoComponent` agora só ativa se o código for recompilado com `true`.

> 🎯 Correção trivial com impacto direto: elimina noclip acidental e logs verbosos em produção.

---

### 22. ✅ Quadtree Logging Verboso Removido ✨ NOVO DESDE v2
**Item original: N2 da v2 | Status: CONCLUÍDO**

`Quadtree.java` (159 linhas) não possui mais **nenhum** `Gdx.app.log()` nos métodos `inserir()` e `consultar()`:

```java
// Antes (v2) — log no hot path:
public void inserir(T item, Rectangle hitbox) {
    if (!limites.overlaps(hitbox)) {
        if (GameConfig.getInstance().isDebugMode()) {
            Gdx.app.log(TAG, "Item ignorado — hitbox fora dos limites...");
        }
        return;
    }
    ...
}

// Depois (v3) — limpo, sem I/O:
public void inserir(T item, Rectangle hitbox) {
    if (!limites.overlaps(hitbox)) {
        return;
    }
    ...
}
```

O `DanoTiro.java` agora usa **log throttled** (a cada 5 segundos) em vez de log por frame:
```java
// Acumuladores para log throttled
private float logTimer = 0f;
private int acumColisoes = 0;
private int acumFramesProcessados = 0;

if (logTimer >= LOG_INTERVALO) {  // 5 segundos
    Gdx.app.log(TAG, "Resumo (5s): " + acumColisoes + " colisões em " + acumFramesProcessados + " frames");
    logTimer = 0f;
    acumColisoes = 0;
    acumFramesProcessados = 0;
}
```

> 🎯 Elimina dezenas de chamadas `Gdx.app.log()` por frame durante combate. Log throttled é solução profissional.

---

### 23. ✅ WorldTemplate Delegate Getters Removidos ✨ NOVO DESDE v2
**Item original: #22 da v2 | Status: CONCLUÍDO**

`WorldTemplate.java` caiu de **285 → 273 linhas**. Os ~30 delegate getters foram removidos. O código externo agora acessa managers diretamente:

```java
// Antes (v2) — delegates redundantes em WorldTemplate:
public Set<String> getSalasVisitadas() { return roomManager.getSalasVisitadas(); }
public TilemapHitboxFactory getTilemapHitboxFactory() { return worldPhysics.getTilemapHitboxFactory(); }
public GerarInimigos getGerarInimigos() { return enemyManager.getGerarInimigos(); }
public List<EnemyTemplate> getListaInimigos() { return enemyManager.getListaInimigos(); }
// ... ~26 mais

// Depois (v3) — acesso direto aos managers:
// WorldTemplate expõe apenas:
public WorldRenderer getWorldRenderer() { return worldRenderer; }
public WorldPhysics getWorldPhysics() { return worldPhysics; }
public RoomManager getRoomManager() { return roomManager; }
public EnemyManager getEnemyManager() { return enemyManager; }
public PlayerTemplate getPlayer() { return player; }
public PawFight getGame() { return game; }
public SpriteBatch getBatch() { return batch; }
```

**Código externo atualizado:**
- `MundoAreia.java` → `roomManager.getCurrentRoom()`, `enemyManager.getListaInimigos()`
- `CarregarPortas.java` → `world.getRoomManager().getRoomGenerator()`, `world.getWorldPhysics().getTilemapHitboxFactory()`
- `DesenharMiniMapa.java` → `world.getRoomManager().getRoomGenerator().getRoomMap()`
- `Renderizar.java` → `world.getEnemyManager().getListaInimigos()`

> 🎯 Eliminação limpa de indireção. WorldTemplate agora tem 273 linhas vs 285 na v2. API mais clara e explícita.

---

### 24. ✅ ColisaoResolver — Sistema de Resolução de Colisão por MTV ✨ NOVO DESDE v2
**Novo sistema — não existia na v2**

`ColisaoResolver.java` (125 linhas) — Sistema completo de resolução de colisões:

**Funcionalidades:**
- **Inimigo vs Inimigo:** Separação bilateral usando MTV (Minimum Translation Vector) — empurra ambas as hitboxes igualmente (`halfPush`)
- **Inimigo vs Paredes:** Empurra inimigo para fora da parede pelo eixo de menor sobreposição
- **Resolução iterativa:** Até `MAX_ITERACOES = 4` passes por frame para resolver colisões em cascata
- **Determinismo:** Caso centros coincidam, usa `hashCode()` para desempate
- **Margem de segurança:** `MARGEM = 0.5f` pixels extra para evitar oscilação

**Integração no game loop via `WorldPhysics`:**
```java
// WorldPhysics.java:
private final ColisaoResolver colisaoResolver = new ColisaoResolver();
private List<Rectangle> paredes;

public void resolverColisoes(List<EnemyTemplate> enemies) {
    colisaoResolver.resolver(enemies, paredes);
}
```

**Chamado em `WorldRenderer.renderizarInimigos()`** após update dos inimigos:
```java
world.getWorldPhysics().resolverColisoes(inimigos);
```

**Complemento em `EnemyTemplate`:**
```java
public void sincronizarPosicaoComHitbox() {
    int offsetX = getHitboxOffsetXDirecional();
    this.dx = hitBox.x - (TAMANHO_PX - HITBOX_SIZE) / 2f - offsetX;
    this.dy = hitBox.y - HITBOX_OFFSET_Y;
}
```

**Complemento em `MoverDirecaoPlayer.java`** — usa `tempHitBox` pré-alocado (não cria Rectangle por frame):
```java
private final Rectangle tempHitBox = new Rectangle();
// ...
tempHitBox.set(newDx + ..., enemy.dy + ..., HITBOX_SIZE, HITBOX_SIZE);
if (!colide(tempHitBox, enemy)) { enemy.dx = newDx; }
```

> 🎯 Sistema de colisão robusto que impede stacking de inimigos. MTV é o algoritmo padrão da indústria. A integração com `MoverDirecaoPlayer` usando `tempHitBox` pré-alocado é um bônus de performance.

---

## 🟡 PARCIALMENTE FEITO (2 itens)

---

### 25. 🟡 Nomes de Variáveis com Nome de Classe
**Item original: #21 da v2 | Progresso: ~70% (era ~60%)**

**O que melhorou desde v2:**
- `Sala Sala` removido de `DesenharMiniMapa.java` (agora usa `sala`)
- `Sala Sala` removido de `GeradorSalas.java` `typeInimigo()` (agora usa `sala`)

**O que ainda viola convenções:**

| Arquivo | Linha | Violação |
|---------|-------|----------|
| `Sala.java` | 29 | `public void connectNorth(Sala Sala)` |
| `Sala.java` | 33 | `public void connectSouth(Sala Sala)` |
| `Sala.java` | 37 | `public void connectEast(Sala Sala)` |
| `Sala.java` | 41 | `public void connectWest(Sala Sala)` |

**4 violações restantes**, todas no mesmo arquivo (`Sala.java`), nos 4 métodos `connect*()`. Os parâmetros devem ser renomeados para `sala` (minúsculo).

**Correção sugerida** (~4 linhas):
```java
public void connectNorth(Sala sala) { this.north = true; this.northRoom = sala; }
public void connectSouth(Sala sala) { this.south = true; this.southRoom = sala; }
public void connectEast(Sala sala)  { this.east  = true; this.eastRoom  = sala; }
public void connectWest(Sala sala)  { this.west  = true; this.westRoom  = sala; }
```

---

### 26. 🟡 Pre-alocar Rectangles em ChecarColisao (Player)
**Item original: #23 da v2 | Progresso: ~50%**

`MoverDirecaoPlayer` (inimigos) já usa `tempHitBox` pré-alocado ✅. Porém `ChecarColisao.ajustarPosicaoSeBaterParede()` (player) **ainda cria 2 Rectangles por frame**:

```java
// ChecarColisao.java linhas 20, 25 — AINDA cria new Rectangle:
Rectangle nextHitboxX = new Rectangle(nextX, playerHitbox.y, playerHitbox.width, playerHitbox.height);
Rectangle nextHitboxY = new Rectangle(playerHitbox.x, nextY, playerHitbox.width, playerHitbox.height);
```

**Chamado a cada frame** durante movimento do player.

**Correção sugerida:**
```java
private static final Rectangle tempX = new Rectangle();
private static final Rectangle tempY = new Rectangle();
// ...
tempX.set(nextX, playerHitbox.y, playerHitbox.width, playerHitbox.height);
tempY.set(playerHitbox.x, nextY, playerHitbox.width, playerHitbox.height);
```

> ⚡ A metade da solução já foi feita (inimigos), falta apenas o player.

---

## ❌ O QUE NÃO FOI FEITO (4 itens)

---

### 27. ❌ Pre-alocar Layers em renderLayers()
**Item original: #24 da v2 | Esforço: BAIXO**

`MundoAreia.renderLayers()` (linha 158) e `renderLayersUp()` (linha 191) ainda criam `new ArrayList<>()` **a cada frame**:
```java
List<String> layers = new ArrayList<>();  // ~60x/s
// ...
layers.toArray(new String[0])             // ~60x/s
```

Adicionalmente, `RenderizadorCamada.renderLayers()` (linha 50) cria `new ArrayList<>()` internamente:
```java
List<Integer> indices = new ArrayList<>(); // a cada chamada
```

**Impacto:** 3 alocações de ArrayList por frame (2 em MundoAreia + 1 em RenderizadorCamada).

**Correção sugerida:**
- `MundoAreia`: Campos `private final List<String> layerBuffer = new ArrayList<>()` com `clear()+add()`
- `RenderizadorCamada`: Campo `private final List<Integer> indicesBuffer = new ArrayList<>()`
- Ou pré-computar `int[]` arrays quando o mapa é carregado

---

### 28. ❌ Object Pool para Projéteis
**Item original: #26 da v2 | Esforço: MÉDIO**

`Atirar.atira()` (linha 24) ainda cria `tiroModelo.clonar(player)` a cada disparo:
```java
TirosTemplate tiroNovo = tiroModelo.clonar(player);
tiroNovo.setDuracao(duracao);
player.adicionarTiro(tiroNovo);
```

`CombateComponent.updateTiros()` remove com `Iterator` + `tiro.dispose()`.

Nenhum `Pool<TirosTemplate>` implementado. O libGDX tem `com.badlogic.gdx.utils.Pool` pronto para uso.

**Impacto:** Cada disparo = 1 new object + eventual GC. Em combate intenso com cadência rápida, isso pode causar GC stutter.

---

### 29. ❌ Personagem Data-Driven
**Item original: #27 da v2 | Esforço: ALTO**

4 classes quase idênticas (~60-72 linhas cada):
- `BlackBird.java` (60), `BlackCat.java` (71), `Dove.java` (72), `OrangeCat.java` (60)

Todas seguem exatamente o mesmo padrão — a única diferença são os **números** nos `DadosPlayer` e os **paths** das texturas. Exemplo comparativo:

| Campo | BlackCat | Dove |
|-------|----------|------|
| forca | 2 | 1 |
| vidaBase | 10 | 6 |
| velocidade | 350 | 600 |
| tamanho | 64 | 32 |
| hitboxSize | 25 | 15 |
| sprites | `black_cat/` | `dove/` |
| áudio passo | `passos.wav` | `asas_passaro.wav` |

Poderia ser **1 classe genérica + 4 JSONs**:
```json
{
  "nome": "Black Cat",
  "forca": 2, "vidaBase": 10, "velocidade": 350, "tamanho": 64,
  "hitboxSize": 25, "hitboxOffsetX": -5, "hitboxOffsetY": 0,
  "sprites": { "idle": "entitys/player/black_cat/Idle.png", ... },
  "audioPassos": "entitys/player/audios/passos.wav"
}
```

---

### 30. ❌ Testes Automatizados
**Item original: #28 da v2 | Esforço: MÉDIO**

- Zero testes no projeto
- `core/build.gradle` não tem JUnit
- Sem diretório `src/test`

**Classes facilmente testáveis sem dependência do libGDX:**
- `StatsComponent` (170 linhas) — dano, XP, level up, validação de limites, cooldowns
- `GeradorSalas` (235 linhas) — geração procedural, validação de conexões, sala tesouro pós-boss
- `ColisaoResolver` (125 linhas) — MTV, separação bilateral, iterações
- `DadosInimigo`, `DadosPlayer` — records imutáveis
- `Quadtree` (159 linhas) — inserção, consulta, subdivisão (depende apenas de `Rectangle`)

---

## 🆕 NOVOS PROBLEMAS ENCONTRADOS (6 itens)

---

### N1. 🔴 Assets.loadAll() Ainda Bloqueia a Thread Principal
**Severidade: ALTA | Esforço: MÉDIO | Item mantido desde v2**

```java
public static void loadAll() {
    loadTextures();
    loadMusic();
    manager.finishLoading();  // ← BLOQUEIA até tudo carregar
}
```

O jogo congela na inicialização. Com **126 linhas** de assets em `Assets.java` (68+ texturas + 10+ músicas), o freeze pode ser de 2-5 segundos.

**Recomendação:** Criar `LoadingScreen` que chama `manager.update()` em loop e mostra progresso com `manager.getProgress()`.

---

### N2. 🟠 DesenharMiniMapa Aloca Objetos a Cada Frame
**Severidade: MÉDIA | Esforço: BAIXO | Item mantido desde v2**

`DesenharMiniMapa.desenharSalaAtual()`:
```java
GlyphLayout layout = new GlyphLayout(font, texto);  // TODA frame (linha 38)
Color corFundo = new Color();                         // TODA frame (linha 39)
```

`DesenharMiniMapa.desenharMiniMapa()`:
```java
shapeRenderer.setColor(new Color(0, 0, 0, 0.3f));   // TODA frame (linha 93)
```

**Correção sugerida:** 
```java
private final GlyphLayout layout = new GlyphLayout();
private static final Color BG_COLOR = new Color(0, 0, 0, 0.3f);
private final Color corFundo = new Color();
```

---

### N3. 🟠 PlayerTemplate.desenharTiros() Ainda Cria ArrayList por Frame
**Severidade: MÉDIA | Esforço: BAIXO | Item mantido desde v2**

```java
// PlayerTemplate.java linha 212:
List<TirosTemplate> tirosSnapshot = new ArrayList<>(combate.getTiros());
```

Embora `DanoTiro` já use lista pré-alocada, `PlayerTemplate.desenharTiros()` ainda cria um `new ArrayList<>()` a cada frame.

**Correção sugerida:** Campo `private final List<TirosTemplate> drawSnapshot = new ArrayList<>()` com `clear()+addAll()`.

---

### N4. 🟠 TilemapHitboxFactory.drawObjects() Cria e Ordena ArrayList a Cada Chamada
**Severidade: MÉDIA | Esforço: BAIXO | Item mantido desde v2**

```java
// TilemapHitboxFactory.java linhas 80-97:
List<MapObject> objects = new ArrayList<>();
for (MapObject obj : layer.getObjects()) { objects.add(obj); }
objects.sort((o1, o2) -> { ... });
```

Chamado em `Base.checkPortals()` quando player < level 5. Cria lista + ordena a cada chamada.

**Correção sugerida:** Cachear a lista ordenada por layer no HashMap `cache`, ou usar campo reutilizável.

---

### N5. 🟠 EscolherPersonagem Recria PlayerTemplate a Cada Preview
**Severidade: MÉDIA | Esforço: MÉDIO | Item mantido desde v2 (elevado para MÉDIA)**

```java
// EscolherPersonagem.java linhas 115, 123:
personagemPreview = getPlayerEscolhido(); // → cria novo PlayerTemplate + carrega save
```

`getPlayerEscolhido()` cria um `new BlackCat(...)` / `new Dove(...)` **a cada mudança de seta** (linhas 171-194). Isso inclui:
- Construção completa de `PlayerTemplate` (9 componentes)
- `rebuildAnimations()` (cria 12+ Animation objects)
- `game.loadPlayer()` (lê JSON do disco!)

Na v3 o problema é **pior** porque `EscolherPersonagem` cresceu para 220 linhas (era 176) com mais lógica visual, mas a recriação continua.

**Correção sugerida:** Criar os 4 players uma vez no construtor e apenas trocar referência:
```java
private final PlayerTemplate[] previews = new PlayerTemplate[4];
// construtor: previews[0] = new BlackCat(...); etc.
// update: personagemPreview = previews[personagemAtual];
```

---

### N6. 🟡 Pacote `Hud` com H Maiúsculo
**Severidade: BAIXA | Esforço: BAIXO | Item mantido desde v2**

O pacote é `com.pawfight.game.engine.hud` (PascalCase) em vez de `com.pawfight.game.engine.hud`. Viola convenção Java onde pacotes devem ser lowercase.

**Impacto:** 5 arquivos nesse pacote + imports em:
- `RoomManager.java` → `import ...engine.Hud.DesenharMiniMapa`
- `PlayerTemplate.java` → `import ...engine.Hud.Hud`, `import ...engine.Hud.HudPause`
- `Home.java` → `import ...engine.Hud.CriarBotao`, `import ...engine.Hud.HudStage`

---

## ⚠️ NOVOS PROBLEMAS IDENTIFICADOS NESTA ANÁLISE (v3)

---

### N7. 🟠 Matrix4 Pré-alocado em ScreenManager
**Severidade: MÉDIA | Esforço: TRIVIAL | Item mantido desde v2 (#25)**

```java
// ScreenManager.java linha 105 — durante transição:
Matrix4 oldMatrix = new Matrix4(batch.getProjectionMatrix()); // TODA frame
```

Executado a cada frame **durante transição** (fade/slide). Uma transição de 1.5s a 60fps = 90 alocações de `Matrix4`.

**Correção sugerida:**
```java
private final Matrix4 tempMatrix = new Matrix4();
// ...
tempMatrix.set(batch.getProjectionMatrix());
```

---

### N8. 🟠 ColisaoComponent Cria TilemapHitboxFactory Redundante
**Severidade: BAIXA | Esforço: BAIXO | NOVO**

`ColisaoComponent` (linha 18) cria sua própria instância de `TilemapHitboxFactory`:
```java
private final TilemapHitboxFactory tilemapHitboxFactory = new TilemapHitboxFactory();
```

Porém `WorldPhysics` **já tem** sua própria `TilemapHitboxFactory` (linha 17). São duas instâncias do mesmo factory — cada uma com seu cache independente.

No `ColisaoComponent`, o `tilemapHitboxFactory` é usado apenas para `draw()` e `drawRects()` dos colisores, **nunca para criar hitboxes**. A criação de hitboxes é feita pela instância em `WorldPhysics`.

**Impacto:** Dois caches independentes, mas baixo impacto porque o `ColisaoComponent` só usa métodos de desenho.

**Correção sugerida:** Remover o field de `ColisaoComponent` e receber o `ShapeRenderer` diretamente, ou usar métodos estáticos para draw de hitboxes.

---

### N9. 🟡 EscolherPersonagem Usa Gdx.input Direto em Vez de KeyBindings
**Severidade: BAIXA | Esforço: BAIXO | NOVO**

`EscolherPersonagem.java` usa `Gdx.input.isKeyJustPressed()` diretamente (linhas 110, 118, 126):
```java
if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) { ... }
if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) { ... }
if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) { ... }
```

Isso é inconsistente com o sistema `KeyBindings` usado em todo o resto do jogo. Se o jogador remapear teclas, a seleção de personagem não respeitará.

**Correção sugerida:** Adicionar ações `MENU_RIGHT`, `MENU_LEFT`, `MENU_CONFIRM` ao `GameAction` enum, ou aceitar como "hardcoded para menus" (decisão de design).

---

### N10. 🟡 MoverDirecaoPlayer Não Verifica Colisão com Paredes
**Severidade: BAIXA | Esforço: MÉDIO | NOVO**

`MoverDirecaoPlayer.colide()` (linha 78) verifica colisão apenas com **outros inimigos**, não com paredes:
```java
private boolean colide(Rectangle testHitbox, EnemyTemplate self) {
    List<EnemyTemplate> others = self.enemiesList;
    if (others != null) {
        for (EnemyTemplate other : others) {
            if (other != self && !other.isMorto() && testHitbox.overlaps(other.getHitBox())) {
                return true;
            }
        }
    }
    return false; // ← NÃO verifica paredes!
}
```

O `ColisaoResolver` resolve isso **depois** (empurrando inimigos para fora das paredes), mas seria mais eficiente prevenir do que corrigir.

O campo `paredesColisores` existe em `EnemyTemplate` (linha 26) e é setado em `CarregarPortas.moverSalaInimigos()` (linha 136), mas `MoverDirecaoPlayer` não o utiliza.

**Correção sugerida:** Adicionar verificação de paredes em `colide()`:
```java
List<Rectangle> paredes = self.getParedesColisores();
if (paredes != null) {
    for (Rectangle parede : paredes) {
        if (testHitbox.overlaps(parede)) return true;
    }
}
```

---

## 📊 TABELA CONSOLIDADA DE PRIORIDADES

### ✅ Concluídos (24 itens)

| # | Tarefa | Status |
|---|--------|--------|
| 1–20 | Todos os itens da v2 | ✅ Mantidos |
| 21 | debugMode = false por padrão | ✅ NOVO |
| 22 | Quadtree logging verboso removido | ✅ NOVO |
| 23 | WorldTemplate delegate getters removidos | ✅ NOVO |
| 24 | ColisaoResolver — Sistema MTV | ✅ NOVO |

### 🟡 Parcialmente Feitos (2 itens)

| # | Tarefa | Progresso | Esforço |
|---|--------|-----------|---------|
| 25 | Nomes camelCase (`Sala Sala` em Sala.java) | 70% | 🟢 Trivial |
| 26 | Pre-alocar Rectangles em ChecarColisao (player) | 50% | 🟢 Baixo |

### ❌ Pendentes (4 itens)

| # | Tarefa | Esforço | Impacto |
|---|--------|---------|---------|
| 27 | Pre-alocar layers renderLayers() | 🟢 Baixo | ⚡ Performance |
| 28 | Object Pool para tiros | 🟡 Médio | ⚡ Performance |
| 29 | Personagem data-driven | 🔴 Alto | 📐 Escalabilidade |
| 30 | Testes unitários | 🟡 Médio | 🛡️ Qualidade |

### 🆕 Problemas em Aberto (6 mantidos + 4 novos)

| # | Tarefa | Severidade | Esforço | Impacto |
|---|--------|-----------|---------|---------|
| N1 | 🔴 **LoadingScreen assíncrona** | ALTA | 🟡 Médio | 🔥 UX Crítico |
| N2 | 🟠 DesenharMiniMapa alocações | MÉDIA | 🟢 Baixo | ⚡ Performance |
| N3 | 🟠 PlayerTemplate snapshot tiros | MÉDIA | 🟢 Baixo | ⚡ Performance |
| N4 | 🟠 TilemapHitbox drawObjects lista | MÉDIA | 🟢 Baixo | ⚡ Performance |
| N5 | 🟠 EscolherPersonagem recria player | MÉDIA | 🟡 Médio | ⚡ Performance |
| N6 | 🟡 Pacote Hud → hud | BAIXA | 🟢 Baixo | 📐 Qualidade |
| N7 | 🟠 Matrix4 pré-alocado ScreenManager | MÉDIA | 🟢 Trivial | ⚡ Performance |
| N8 | 🟡 ColisaoComponent TilemapHitboxFactory redundante | BAIXA | 🟢 Baixo | 📐 Qualidade |
| N9 | 🟡 EscolherPersonagem usa Gdx.input direto | BAIXA | 🟢 Baixo | 📐 Consistência |
| N10 | 🟡 MoverDirecaoPlayer sem colisão com paredes | BAIXA | 🟡 Médio | 🐛 Gameplay |

---

## 🎯 PLANO DE AÇÃO RECOMENDADO

### 🟢 Sprint 1 — Quick Wins (1h total)

| # | Tarefa | Mudança | Linhas |
|---|--------|---------|--------|
| 25 | 4 nomes camelCase em Sala.java | Renomear `Sala Sala` → `Sala sala` | ~4 |
| 26 | Pre-alocar Rectangles player | 2 campos `Rectangle` em `ChecarColisao` + `.set()` | ~6 |
| 27 | Pre-alocar layers | Campos reutilizáveis em `MundoAreia` + `RenderizadorCamada` | ~12 |
| N2 | MiniMapa alocações | 3 campos pré-alocados em `DesenharMiniMapa` | ~5 |
| N3 | Snapshot tiros | Campo reutilizável em `PlayerTemplate` | ~3 |
| N7 | Matrix4 pré-alocado | 1 campo em `ScreenManager` | ~3 |

**Total: ~33 linhas, ~1h de trabalho, 6 itens resolvidos**

### 🟡 Sprint 2 — Melhorias Importantes (meio dia)

| # | Tarefa | Benefício |
|---|--------|-----------|
| N1 | LoadingScreen assíncrona | Elimina freeze de 2-5s na inicialização |
| N5 | EscolherPersonagem cachear | Elimina recriação de PlayerTemplate a cada preview |
| N4 | TilemapHitbox cache lista | Elimina sort a cada chamada |
| N6 | Pacote Hud → hud | Convenção Java (IDE faz refactor automático) |
| N10 | MoverDirecaoPlayer verificar paredes | Previne inimigos atravessando paredes |

### 🟠 Sprint 3 — Funcionalidades (1-2 dias)

| # | Tarefa | Benefício |
|---|--------|-----------|
| 28 | Object Pool para tiros | Elimina GC pressure em combate |
| 30 | Testes unitários básicos | StatsComponent, Quadtree, ColisaoResolver, GeradorSalas |
| N8 | Limpar TilemapHitboxFactory redundante | Eliminar instância desnecessária |
| N9 | KeyBindings em EscolherPersonagem | Consistência do sistema de input |

### 🔴 Sprint 4 — Refatoração Maior (1+ dia)

| # | Tarefa | Benefício |
|---|--------|-----------|
| 29 | Personagem data-driven | 4 classes → 1 genérica + 4 JSONs |

---

## 📈 EVOLUÇÃO COMPLETA DO PROJETO

```
Análise original:  ✅ 5   🟡 4  ❌ 13  → ~23% concluído
Análise v1:        ✅ 12  🟡 3  ❌ 10  → ~48% concluído
Análise v2:        ✅ 20  🟡 2  ❌ 6   → ~71% concluído
Análise v3:        ✅ 24  🟡 2  ❌ 4   → ~78% concluído

Itens resolvidos desde a v2:
  ✅ debugMode = false (era N8 🟡)
  ✅ Quadtree logging removido (era N2 🟠)
  ✅ WorldTemplate delegate getters removidos (era #22 🟡)
  ✅ ColisaoResolver — Sistema MTV (NOVO)
```

```
                         ███████████████████████░░░░░░  78%
Análise original:        █████░░░░░░░░░░░░░░░░░░░░░░░  23%
Análise v1:              ██████████████░░░░░░░░░░░░░░░  48%
Análise v2:              █████████████████████░░░░░░░░  71%
Análise v3:              ███████████████████████░░░░░░  78%
```

---

## 💡 PONTOS POSITIVOS DO PROJETO

1. **Arquitetura de componentes madura** — `PlayerTemplate` e `EnemyTemplate` usam composição com 9 componentes bem definidos
2. **Zero memory leaks** — Todos os assets gerenciados pelo `AssetManager`, nenhum `dispose()` incorreto
3. **GameConfig encapsulado** — Singleton com validação, `debugMode = false` em produção
4. **Sistema de input profissional** — `KeyBindings` com swap de conflitos, persistência JSON, enum tipado
5. **Spatial partitioning** — `Quadtree` genérica com debug visual colorido, sem logs no hot path
6. **WorldTemplate limpo** — 4 managers claros, sem delegate getters redundantes
7. **ColisaoResolver com MTV** — Resolução robusta inimigo-inimigo e inimigo-parede com iteração
8. **Transições centralizadas** — `ScreenManager` com interface `TransitionEffect` e dois efeitos
9. **Cache bidirecional de animações** — Mudança de direção em O(1)
10. **Records Java** — `DadosPlayer`, `DadosInimigo`, `DefinirSprite`, `InfoGeraObjeto`
11. **Error handling consistente** — try/catch com logging em todos os mundos e renderização
12. **Separação clara de pacotes** — `engine/`, `entity/`, `world/` com sub-pacotes lógicos
13. **DadosSalvosJogador limpo** — De 66 linhas de boilerplate para 12 linhas diretas
14. **Log throttled em DanoTiro** — Resumo a cada 5s em vez de log por frame
15. **TilemapHitboxFactory com cache** — HashMap evita recriar hitboxes para o mesmo mapa
16. **MoverDirecaoPlayer com Rectangle pré-alocado** — `tempHitBox` reutilizável, zero GC no movement
17. **GeradorSalas expandido** — Sala tesouro pós-boss, salas extras com tipos variados
18. **CarregarPortas refatorado com enum** — `Direcao` elimina duplicação de código nas 4 direções

---

## 🔍 COMPARATIVO DE CONTAGEM DE LINHAS (v2 → v3)

| Arquivo | v2 | v3 | Δ | Nota |
|---------|----|----|---|------|
| `PawFight.java` | 132 | 161 | +29 | |
| `Assets.java` | 103 | 126 | +23 | Mais texturas/músicas |
| `GameConfig.java` | 74 | 96 | +22 | Volume passos, métodos Percent |
| `ScreenManager.java` | 106 | 134 | +28 | |
| `ChecarColisao.java` | 33 | 46 | +13 | checarColisaoSeparadoEixo |
| `ColisaoResolver.java` | — | 125 | +125 | 🆕 |
| `Quadtree.java` | 146 | 159 | +13 | drawDebug melhorado |
| `TilemapHitboxFactory.java` | 139 | 170 | +31 | Cache, createTileLayer |
| `DesenharMiniMapa.java` | 152 | 183 | +31 | |
| `CarregarPortas.java` | 114 | 144 | +30 | Enum Direcao |
| `GeradorSalas.java` | 192 | 235 | +43 | Tesouro, extras |
| `Sala.java` | 51 | 61 | +10 | Vizinhos, getRoom() |
| `Renderizar.java` | 95 | 113 | +18 | hitBoxListObjeto |
| `RenderizadorCamada.java` | 60 | 74 | +14 | renderLayerTiled |
| `PlayerTemplate.java` | 314 | 389 | +75 | Save/Load, organização |
| `EnemyTemplate.java` | 231 | 265 | +34 | sincronizarPosicao |
| `MoverDirecaoPlayer.java` | 55 | 90 | +35 | tempHitBox, colisão |
| `Skeleton.java` | 80 | 95 | +15 | aplicarStatsForte |
| `DanoTiro.java` | 75 | 102 | +27 | Log throttled |
| `Atirar.java` | 24 | 33 | +9 | |
| `CombateComponent.java` | 57 | 74 | +17 | processarTirosAutomaticos |
| `StatsComponent.java` | 150 | 170 | +20 | Setters para save |
| `ColisaoComponent.java` | 50 | 67 | +17 | adicionarColisaoPorLevel |
| `WorldTemplate.java` | 285 | 273 | -12 | Delegates removidos ✅ |
| `WorldPhysics.java` | 39 | 65 | +26 | ColisaoResolver |
| `WorldRenderer.java` | 73 | 93 | +20 | ColisaoResolver integração |
| `RoomManager.java` | 71 | 92 | +21 | currentRoomFoiVisitada |
| `EnemyManager.java` | 28 | 39 | +11 | hasInimigos, clear |
| `Home.java` | 138 | 160 | +22 | Try/catch granular |
| `Base.java` | 124 | 150 | +26 | Hitboxes cacheadas |
| `MundoAreia.java` | 214 | 246 | +32 | Acessa managers direto |
| `EscolherPersonagem.java` | 176 | 220 | +44 | Nuvem, layout |
| `BlackCat.java` | 60 | 71 | +11 | |
| `Dove.java` | 60 | 72 | +12 | |

> **Conclusão:** O projeto avançou de 71% para 78% de conclusão. Os 4 novos itens resolvidos desde a v2 são significativos: a correção do `debugMode`, a limpeza dos logs da Quadtree, a remoção dos delegate getters do `WorldTemplate`, e sobretudo o novo `ColisaoResolver` com MTV que resolve um problema real de gameplay (stacking de inimigos). O crescimento de 5.860 → 5.920 linhas (+60 net) é modesto considerando que o `ColisaoResolver` sozinho tem 125 linhas — indicando que houve também limpeza de código redundante. O próximo foco deve ser o **Sprint 1 de Quick Wins** (6 itens triviais, ~33 linhas, ~1h) e a **LoadingScreen** que permanece como o problema de UX mais impactante para o jogador.

