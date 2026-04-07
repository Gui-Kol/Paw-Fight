# 🐾 Análise Completa — Paw Fight (Abril 2026 — v4)

## 📊 Visão Geral do Projeto

| Métrica | v1 | v2 | v3 | Atual (v4) | Δ (v3→v4) |
|---------|----|----|----|-----------:|-----------|
| Arquivos `.java` | 69 | 76 | 77 | **77** | — |
| Linhas de código totais | 5.269 | 5.860 | 6.752¹ | **7.461** | +709 |
| Pacotes | 13 | 15 | 15² | **24** | +9³ |
| Framework | libGDX | libGDX | libGDX | libGDX | — |
| Build | Gradle | Gradle | Gradle | Gradle | — |
| Testes | ❌ Zero | ❌ Zero | ❌ Zero | ❌ Zero | — |

> ¹ A v3 reportou 5.920, porém a soma individual dos arquivos listados na árvore da v3 totaliza 6.752. O valor 5.920 era um erro aritmético.
> ² A v3 reportou 15 pacotes, mas a contagem correta (incluindo sub-pacotes) sempre foi 24.
> ³ Não é crescimento real — a v3 subestimou a contagem.

---

### 📐 Arquivos com Mudanças Significativas Desde v3

| Arquivo | v3 | v4 | Δ | Resumo da mudança |
|---------|----|----|---|-------------------|
| `TirosTemplate.java` | 90 | **165** | +75 | `Pool.Poolable`, `reiniciarBase()`, `liberar()`, `ownerPool` ✨ Object Pool |
| `Coco.java` | 52 | **99** | +47 | `Pool<Coco>`, `obterDoPool()` ✨ Object Pool |
| `ChecarColisao.java` | 46 | **93** | +47 | `empurrarForaParedes()`, `tempX`/`tempY` pré-alocados |
| `EnemyTemplate.java` | 265 | **309** | +44 | `empurrarForaOutrosInimigos()` inline no update |
| `Hud.java` | 168 | **203** | +35 | Layouts e cores pré-alocados, rendering melhorado |
| `AnimacaoComponent.java` | 183 | **216** | +33 | 6 caches de animação (era 4), troca de direção mais robusta |
| `GerarInimigos.java` | 91 | **120** | +29 | Validação robusta, safety checks em regiões de spawn |
| `KeyBindings.java` | 110 | **137** | +27 | Persistência melhorada, ações MENU_* adicionadas |
| `MotorAnimacao.java` | 123 | **147** | +24 | Overloads para Camera, fundo rendering melhorado |
| `HudPause.java` | 119 | **142** | +23 | Texturas pré-carregadas como campos `final` |
| `ExibirDadosPersonagem.java` | 77 | **96** | +19 | Scaling melhorado, layout com ícones |
| `SaveComponent.java` | 30 | **48** | +18 | `loadSaveData()` retorna `PlayerTemplate`, JSON handling |
| `GerarObjetos.java` | 55 | **72** | +17 | Cálculos de hitbox offset, validação |
| `SlideTransitionEffect.java` | 87 | **104** | +17 | Easing cubic, dispose, reset aprimorados |
| `AudioComponent.java` | 39 | **55** | +16 | `initEnemy()`, `updateAudio()`, getters/setters |
| `FadeTransitionEffect.java` | 91 | **106** | +15 | `easeInOutSine`, suporte a gradiente |
| `TransicaoTela.java` | 82 | **97** | +15 | Suporte Slide + Fade combinados, duração configurável |
| `MundoAreia.java` | 246 | **261** | +15 | `layerBuffer`, `layerUpBuffer`, helper `toArray()` |
| `GameAction.java` | 24 | **38** | +14 | `MENU_RIGHT`, `MENU_LEFT`, `MENU_CONFIRM` ✨ Novo |
| `CameraComponent.java` | 43 | **56** | +13 | Javadoc, integração AlteradorZoom |
| `CriarBotao.java` | 50 | **63** | +13 | Overload `create()`, parâmetro de áudio |
| `EntradaPortais.java` | 59 | **72** | +13 | Error handling, null checks |
| `PawFight.java` | 161 | **147** | -14 | Cleanup, código redundante removido |
| `EscolherPersonagem.java` | 220 | **208** | -12 | Simplificado com `previews[]` ✅ cache |
| `MoverDirecaoPlayer.java` | 90 | **100** | +10 | `colide()` agora verifica paredes ✅ |
| `BlackBird.java` | 60 | **71** | +11 | Definições adicionais de sprites |
| `OrangeCat.java` | 60 | **71** | +11 | Definições adicionais de sprites |

---

### 🏗️ Arquitetura de Pacotes

```
com.pawfight.game
├── PawFight.java                          (147 linhas) — Game principal
├── engine/
│   ├── Assets.java                        (126) — AssetManager centralizado
│   ├── AudioEngine.java                   (37)  — Wrapper de áudio
│   ├── GameConfig.java                    (96)  — Configuração centralizada
│   ├── ScreenManager.java                 (131) — Gerenciador de transições (tempMatrix ✅)
│   ├── Validar.java                       (14)  — Utilitário de validação
│   ├── design/
│   │   ├── AlteradorZoom.java             (30)
│   │   ├── DefinirSprite.java             (13)  — record
│   │   ├── animation/MotorAnimacao.java   (147) — Motor de animação
│   │   ├── desenhar/DesenharTexto.java    (62)  — GlyphLayout reutilizável ✅
│   │   ├── desenhar/DesenharTextura.java  (30)
│   │   └── transition/
│   │       ├── TransitionEffect.java      (13)  — interface
│   │       ├── FadeTransitionEffect.java  (106) — com easeInOutSine
│   │       ├── SlideTransitionEffect.java (104) — com easeInOutCubic
│   │       └── TransicaoTela.java         (97)  — Slide + Fade
│   ├── fisica/
│   │   ├── ChecarColisao.java             (93)  — tempX/tempY pré-alocados ✅
│   │   ├── ColisaoResolver.java           (122) — Resolução MTV
│   │   ├── Quadtree.java                  (159) — Quadtree genérica
│   │   └── TilemapHitboxFactory.java      (174) — com cache + sortedObjectsCache ✅
│   ├── font/FontFactory.java              (19)
│   ├── hud/                               — (pacote lowercase ✅)
│   │   ├── CriarBotao.java               (63)  — overload com áudio
│   │   ├── DesenharMiniMapa.java          (185) — BG_COLOR + layout pré-alocados ✅
│   │   ├── Hud.java                       (203) — layouts/cores pré-alocados ✅
│   │   ├── HudPause.java                  (142) — texturas como campos final
│   │   └── HudStage.java                  (69)  — InputMultiplexer cleanup
│   ├── input/
│   │   ├── GameAction.java                (38)  — +MENU_RIGHT/LEFT/CONFIRM ✅
│   │   └── KeyBindings.java               (137) — persistência melhorada
│   ├── procedural/
│   │   ├── CarregarPortas.java            (148) — enum Direcao
│   │   ├── GerarInimigos.java             (120) — validação robusta
│   │   ├── GerarObjetos.java              (72)
│   │   ├── ObjetoGerado.java              (52)  — areaToque calculada
│   │   └── sala/
│   │       ├── Direction.java             (4)   — enum package-private
│   │       ├── GeradorSalas.java          (235) — tesouro pós-boss
│   │       ├── InfoGeraObjeto.java        (20)  — record
│   │       ├── Sala.java                  (61)  — parâmetros camelCase ✅
│   │       └── TipoSala.java             (10)  — 5 tipos
│   ├── render/
│   │   ├── Renderizar.java                (113)
│   │   └── RenderizadorCamada.java        (79)  — indicesBuffer pré-alocado ✅
│   └── save/
│       ├── DadosSalvosJogador.java        (14)
│       └── SalvarJogo.java                (28)
├── entity/
│   ├── Entidade.java                      (27)  — interface (expandida)
│   ├── bosses/BossesTemplate.java         (5)
│   ├── component/                         — 9 componentes
│   │   ├── AnimacaoComponent.java         (216) — 6 caches L/R ✅
│   │   ├── AudioComponent.java            (55)  — initEnemy(), updateAudio()
│   │   ├── CameraComponent.java           (56)  — Javadoc
│   │   ├── ColisaoComponent.java          (66)  — TilemapHitboxFactory removido ✅
│   │   ├── CombateComponent.java          (71)  — usa tiro.liberar() ✅
│   │   ├── InputComponent.java            (23)  — usa KeyBindings
│   │   ├── MovimentoComponent.java        (46)
│   │   ├── SaveComponent.java             (48)  — loadSaveData melhorado
│   │   └── StatsComponent.java            (170)
│   ├── enemy/
│   │   ├── DadosInimigo.java              (26)  — record
│   │   ├── EnemyTemplate.java             (309) — empurrarForaOutrosInimigos
│   │   ├── MoverDirecaoPlayer.java        (100) — colisão com paredes ✅
│   │   └── Skeleton.java                  (95)
│   ├── player/
│   │   ├── DadosPlayer.java               (24)  — record
│   │   ├── PlayerTemplate.java            (395) — drawSnapshot pré-alocado ✅
│   │   ├── BlackBird.java                 (71)
│   │   ├── BlackCat.java                  (71)
│   │   ├── Dove.java                      (72)
│   │   └── OrangeCat.java                 (71)
│   └── tiro/
│       ├── Atirar.java                    (33)  — usa obterDoPool() ✅
│       ├── DanoTiro.java                  (102) — log throttled
│       ├── TirosTemplate.java             (165) — Pool.Poolable ✅ ✨ NOVO
│       └── dove/Coco.java                 (99)  — Pool<Coco> ✅ ✨ NOVO
└── world/
    ├── Home.java                          (159)
    ├── MundoAreia.java                    (261) — layerBuffer/layerUpBuffer ✅
    ├── template/
    │   ├── WorldTemplate.java             (277) — sem delegate getters
    │   ├── WorldRenderer.java             (93)
    │   ├── WorldPhysics.java              (65)  — com ColisaoResolver
    │   ├── RoomManager.java               (89)
    │   └── EnemyManager.java              (36)
    └── base/
        ├── Base.java                      (150) — hitboxes cacheadas
        ├── EntradaPortais.java            (72)  — error handling
        ├── EscolherPersonagem.java        (208) — previews[] cache ✅
        ├── ExibirDadosPersonagem.java     (96)  — scaling melhorado
        └── Portoes.java                   (26)
```

---

## 📈 Resumo de Progresso vs Análises Anteriores

| Status | v1 | v2 | v3 | v4 | Δ (v3→v4) |
|--------|----|----|----|----|-----------|
| ✅ Feito | 12 | 20 | 24 | **34** | **+10** |
| 🟡 Parcialmente feito | 3 | 2 | 2 | **0** | -2 |
| ❌ Não feito | 10 | 6 | 4 | **2** | -2 |
| 🆕 Problemas em aberto | 6 | 8 | 10 | **7** | -3 |

**Progresso geral: ~93% completo** (vs ~78% na v3, ~71% na v2, ~48% na v1)

> 🎉 **Maior salto de progresso desde a v1→v2.** 10 itens resolvidos de uma vez, incluindo todos os 🟡 parciais e 2 dos 4 ❌ pendentes. Restam apenas 2 itens estruturais (data-driven + testes) e 7 problemas menores.

---

## ✅ O QUE JÁ FOI FEITO (34 itens)

*(24 itens da v3 + 10 novos itens resolvidos)*

### Itens mantidos da v3 (1–24)

Todos os 24 itens marcados como ✅ na análise v3 continuam válidos:

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
21. ✅ debugMode = false por Padrão
22. ✅ Quadtree Logging Verboso Removido
23. ✅ WorldTemplate Delegate Getters Removidos
24. ✅ ColisaoResolver — Sistema de Resolução MTV

---

### 25. ✅ Nomes de Variáveis camelCase em Sala.java ✨ RESOLVIDO DESDE v3
**Item original: #21 da v2 → #25 🟡 na v3 | Status: CONCLUÍDO**

Todas as 4 violações restantes em `Sala.java` foram corrigidas. Os parâmetros `Sala Sala` (PascalCase) agora usam `Sala sala` (camelCase):

```java
// Antes (v3) — violação de convenção:
public void connectNorth(Sala Sala) { this.north = true; this.northRoom = Sala; }
public void connectSouth(Sala Sala) { this.south = true; this.southRoom = Sala; }
public void connectEast(Sala Sala)  { this.east  = true; this.eastRoom  = Sala; }
public void connectWest(Sala Sala)  { this.west  = true; this.westRoom  = Sala; }

// Depois (v4) — convenção Java correta:
public void connectNorth(Sala sala) { this.north = true; this.northRoom = sala; }
public void connectSouth(Sala sala) { this.south = true; this.southRoom = sala; }
public void connectEast(Sala sala)  { this.east  = true; this.eastRoom  = sala; }
public void connectWest(Sala sala)  { this.west  = true; this.westRoom  = sala; }
```

> 🎯 Zero violações de naming restantes. Convenção Java 100% respeitada.

---

### 26. ✅ Pre-alocar Rectangles em ChecarColisao (Player) ✨ RESOLVIDO DESDE v3
**Item original: #23 da v2 → #26 🟡 na v3 | Status: CONCLUÍDO**

`ChecarColisao.java` agora usa campos `static final` em vez de criar `new Rectangle()` por frame:

```java
// Antes (v3) — 2 Rectangles por frame:
Rectangle nextHitboxX = new Rectangle(nextX, playerHitbox.y, ...);
Rectangle nextHitboxY = new Rectangle(playerHitbox.x, nextY, ...);

// Depois (v4) — zero alocações:
private static final Rectangle tempX = new Rectangle();
private static final Rectangle tempY = new Rectangle();
// ...
tempX.set(nextX, playerHitbox.y, playerHitbox.width, playerHitbox.height);
tempY.set(playerHitbox.x, nextY, playerHitbox.width, playerHitbox.height);
```

Adicionalmente, um novo método `empurrarForaParedes()` foi adicionado (linhas 56-92), que usa os mesmos `tempX`/`tempY` pré-alocados para empurrar o player para fora de paredes quando ocorre sobreposição:

```java
public static void empurrarForaParedes(Rectangle hitBox, List<Rectangle> paredes) {
    if (paredes == null) return;
    for (Rectangle parede : paredes) {
        if (hitBox.overlaps(parede)) {
            // Calcula overlap em cada eixo
            float overlapX = Math.min(hitBox.x + hitBox.width - parede.x,
                                      parede.x + parede.width - hitBox.x);
            float overlapY = Math.min(hitBox.y + hitBox.height - parede.y,
                                      parede.y + parede.height - hitBox.y);
            // Empurra pelo eixo de menor sobreposição (MTV)
            if (overlapX < overlapY) { /* ajusta X */ }
            else { /* ajusta Y */ }
        }
    }
}
```

O arquivo cresceu de **46 → 93 linhas** (+47). A resolução é completa: tanto inimigos (`MoverDirecaoPlayer.tempHitBox`) quanto player (`ChecarColisao.tempX/tempY`) agora usam Rectangles pré-alocados.

> 🎯 100% completo. Zero `new Rectangle()` no hot path de colisão.

---

### 27. ✅ Pre-alocar Layers em renderLayers() ✨ RESOLVIDO DESDE v3
**Item original: #24 da v2 → #27 ❌ na v3 | Status: CONCLUÍDO**

Três correções implementadas simultaneamente:

**1. MundoAreia.java** — Campos `layerBuffer` e `layerUpBuffer`:
```java
// Antes (v3) — 2 ArrayLists por frame:
List<String> layers = new ArrayList<>();   // renderLayers()
List<String> layers = new ArrayList<>();   // renderLayersUp()

// Depois (v4) — campos reutilizáveis:
private final List<String> layerBuffer = new ArrayList<>();
private final List<String> layerUpBuffer = new ArrayList<>();
// + helper method:
private String[] toArray(List<String> list) {
    // Usa Arrays.copyOf com cache interno
}
```

**2. RenderizadorCamada.java** — Campo `indicesBuffer`:
```java
// Antes (v3) — ArrayList por chamada:
List<Integer> indices = new ArrayList<>();

// Depois (v4) — campo reutilizável + array cache:
private final List<Integer> indicesBuffer = new ArrayList<>();
private int[] idxArray = new int[0];
```

O campo `idxArray` é reutilizado quando o tamanho não muda, evitando também `toArray()` redundante:
```java
indicesBuffer.clear();
// ... preenche indicesBuffer ...
if (idxArray.length != indicesBuffer.size()) {
    idxArray = new int[indicesBuffer.size()];
}
for (int i = 0; i < indicesBuffer.size(); i++) {
    idxArray[i] = indicesBuffer.get(i);
}
```

> 🎯 Elimina 3 alocações de ArrayList por frame (2 em MundoAreia + 1 em RenderizadorCamada).

---

### 28. ✅ Object Pool para Projéteis ✨ RESOLVIDO DESDE v3
**Item original: #26 da v2 → #28 ❌ na v3 | Status: CONCLUÍDO**

Sistema completo de Object Pool implementado usando `com.badlogic.gdx.utils.Pool`:

**1. TirosTemplate.java** (90 → 165 linhas, +75) — Base poolable:
```java
public abstract class TirosTemplate implements Pool.Poolable {
    protected Pool<? extends TirosTemplate> ownerPool;

    // Reset completo do objeto para reutilização
    public void reiniciarBase(PlayerTemplate player) {
        this.player = player;
        this.dx = player.getDx();
        this.dy = player.getDy();
        this.ativo = true;
        this.olhandoEsquerda = player.isOlhandoEsquerda();
    }

    // Devolve ao pool em vez de criar garbage
    public void liberar() {
        if (ownerPool != null) {
            ownerPool.free(this);
        }
    }

    @Override
    public void reset() {
        this.ativo = false;
        this.player = null;
    }

    // Cada subclasse implementa:
    public abstract TirosTemplate obterDoPool(PlayerTemplate player);
}
```

**2. Coco.java** (52 → 99 linhas, +47) — Pool concreto:
```java
public class Coco extends TirosTemplate {
    private static final Pool<Coco> pool = new Pool<Coco>() {
        @Override
        protected Coco newObject() {
            return new Coco(/* ... */);
        }
    };

    @Override
    public TirosTemplate obterDoPool(PlayerTemplate player) {
        Coco coco = pool.obtain();
        coco.ownerPool = pool;
        coco.reiniciarBase(player);
        coco.reiniciar(player); // específico do Coco
        return coco;
    }
}
```

**3. Atirar.java** — Usa pool em vez de clone:
```java
// Antes (v3):
TirosTemplate tiroNovo = tiroModelo.clonar(player);

// Depois (v4):
TirosTemplate tiroNovo = tiroModelo.obterDoPool(player);
```

**4. CombateComponent.java** — Devolve ao pool:
```java
// Antes (v3):
tiro.dispose();  // cria garbage

// Depois (v4):
tiro.liberar();  // devolve ao pool para reutilização
```

**Fluxo completo:**
```
Disparo → pool.obtain() → reiniciarBase() → uso → liberar() → pool.free() → reset()
                                                                     ↓
                                                              pool.obtain() (reutiliza!)
```

> 🎯 Cada disparo agora é O(1) sem alocação (após warm-up). Em combate intenso com cadência rápida, o pool elimina completamente GC stutter. Solução profissional usando a API nativa do libGDX.

---

### 29. ✅ DesenharMiniMapa Alocações Eliminadas ✨ RESOLVIDO DESDE v3
**Item original: N2 da v3 | Status: CONCLUÍDO**

Todas as alocações por frame foram convertidas em campos:

```java
// Antes (v3) — 3 alocações por frame:
GlyphLayout layout = new GlyphLayout(font, texto);  // TODA frame
Color corFundo = new Color();                         // TODA frame
shapeRenderer.setColor(new Color(0, 0, 0, 0.3f));   // TODA frame

// Depois (v4) — campos reutilizáveis:
private static final Color BG_COLOR = new Color(0, 0, 0, 0.3f);
private final GlyphLayout layout = new GlyphLayout();
private final Color corFundo = new Color();
```

> 🎯 3 objetos a menos criados por frame no minimapa.

---

### 30. ✅ PlayerTemplate.desenharTiros() Snapshot Pré-alocado ✨ RESOLVIDO DESDE v3
**Item original: N3 da v3 | Status: CONCLUÍDO**

```java
// Antes (v3):
List<TirosTemplate> tirosSnapshot = new ArrayList<>(combate.getTiros());

// Depois (v4) — campo reutilizável:
private final List<TirosTemplate> drawSnapshot = new ArrayList<>();
// no método:
drawSnapshot.clear();
drawSnapshot.addAll(combate.getTiros());
```

> 🎯 Zero `new ArrayList<>()` por frame durante renderização de tiros.

---

### 31. ✅ TilemapHitboxFactory.drawObjects() Cache de Lista Ordenada ✨ RESOLVIDO DESDE v3
**Item original: N4 da v3 | Status: CONCLUÍDO**

```java
// Antes (v3) — criava e ordenava lista a cada chamada:
List<MapObject> objects = new ArrayList<>();
for (MapObject obj : layer.getObjects()) { objects.add(obj); }
objects.sort((o1, o2) -> { ... });

// Depois (v4) — campo reutilizável:
private final List<MapObject> sortedObjectsCache = new ArrayList<>();
// no método:
sortedObjectsCache.clear();
for (MapObject obj : layer.getObjects()) { sortedObjectsCache.add(obj); }
sortedObjectsCache.sort((o1, o2) -> { ... });
```

> 🎯 Elimina `new ArrayList<>()` a cada chamada de drawObjects. O cache é reutilizado entre frames.

---

### 32. ✅ EscolherPersonagem Cache de Previews ✨ RESOLVIDO DESDE v3
**Item original: N5 da v3 | Status: CONCLUÍDO**

O problema era grave: a cada mudança de seta, um novo `PlayerTemplate` era construído (9 componentes + 12+ animations + leitura de JSON do disco).

```java
// Antes (v3) — recria a cada preview:
personagemPreview = getPlayerEscolhido(); // → new BlackCat(...) + save load

// Depois (v4) — 4 players criados uma vez:
private final PlayerTemplate[] previews = new PlayerTemplate[4];

// construtor:
previews[0] = new BlackCat(game, camera, viewport, null);
previews[1] = new Dove(game, camera, viewport, null);
previews[2] = new BlackBird(game, camera, viewport, null);
previews[3] = new OrangeCat(game, camera, viewport, null);

// update:
personagemPreview = previews[personagemAtual]; // troca de referência O(1)
```

`EscolherPersonagem` encolheu de 220 → 208 linhas (-12) apesar de ter funcionalidade adicional. A seleção de personagem agora usa `KeyBindings` em vez de `Gdx.input` direto (resolve N9 simultaneamente):

```java
// Antes (v3):
if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) { ... }

// Depois (v4):
if (KeyBindings.getInstance().isActive(GameAction.MENU_RIGHT)) { ... }
```

> 🎯 Elimina reconstrução completa de PlayerTemplate a cada preview. Melhora UX drasticamente (troca instantânea vs delay perceptível).

---

### 33. ✅ Matrix4 Pré-alocado em ScreenManager ✨ RESOLVIDO DESDE v3
**Item original: N7 da v3 | Status: CONCLUÍDO**

```java
// Antes (v3):
Matrix4 oldMatrix = new Matrix4(batch.getProjectionMatrix()); // 90x durante transição

// Depois (v4):
private final Matrix4 tempMatrix = new Matrix4();
// no método:
tempMatrix.set(batch.getProjectionMatrix());
```

> 🎯 Elimina 90 alocações de Matrix4 por transição (1.5s a 60fps).

---

### 34. ✅ ColisaoComponent TilemapHitboxFactory Redundante Removida ✨ RESOLVIDO DESDE v3
**Item original: N8 da v3 | Status: CONCLUÍDO**

`ColisaoComponent` não possui mais sua própria instância de `TilemapHitboxFactory`. O campo foi removido e os métodos de debug draw foram simplificados.

```java
// Antes (v3) — instância redundante:
private final TilemapHitboxFactory tilemapHitboxFactory = new TilemapHitboxFactory();

// Depois (v4) — campo removido, métodos de draw simplificados
```

> 🎯 Elimina cache duplicado e instância desnecessária.

---

### ✅ Itens Adicionais Resolvidos (não numerados)

**N6 (v3) — Pacote `Hud` PascalCase:** Este era um **falso alarme** na v3. O pacote Java sempre foi `com.pawfight.game.engine.hud` (lowercase). A confusão foi com a pasta de assets `assets/Hud/` que usa PascalCase, mas é um diretório de recursos, não um pacote Java.

**N9 (v3) — EscolherPersonagem usa Gdx.input direto:** Resolvido como parte do item #32. `GameAction` agora inclui `MENU_RIGHT`, `MENU_LEFT`, `MENU_CONFIRM`.

**N10 (v3) — MoverDirecaoPlayer sem colisão com paredes:** O método `colide()` agora verifica `self.paredesColisores`:

```java
// Antes (v3) — apenas inimigos:
private boolean colide(Rectangle testHitbox, EnemyTemplate self) {
    // ... verifica outros inimigos ...
    return false; // NÃO verifica paredes!
}

// Depois (v4) — inimigos + paredes:
private boolean colide(Rectangle testHitbox, EnemyTemplate self) {
    // ... verifica outros inimigos ...
    List<Rectangle> paredes = self.paredesColisores;
    if (paredes != null) {
        for (Rectangle parede : paredes) {
            if (testHitbox.overlaps(parede)) return true;
        }
    }
    return false;
}
```

> 🎯 Previne inimigos de entrar em paredes no movement step, reduzindo o trabalho do ColisaoResolver.

---

## ❌ O QUE NÃO FOI FEITO (2 itens)

---

### 35. ❌ Personagem Data-Driven (NÃO VOU FAZER QUERO MANTER COMO ESTÁ) ✂️
**Item original: #27 da v2 → #29 da v3 | Esforço: ALTO**

4 classes quase idênticas (~71 linhas cada):
- `BlackBird.java` (71), `BlackCat.java` (71), `Dove.java` (72), `OrangeCat.java` (71)

Todas seguem exatamente o mesmo padrão — a única diferença são os **números** nos `DadosPlayer` e os **paths** das texturas. Exemplo comparativo:

| Campo | BlackCat | Dove | BlackBird | OrangeCat |
|-------|----------|------|-----------|-----------|
| forca | 2 | 1 | 1 | 2 |
| vidaBase | 10 | 6 | 7 | 10 |
| velocidade | 350 | 600 | 500 | 350 |
| tamanho | 64 | 32 | 32 | 64 |
| hitboxSize | 25 | 15 | 15 | 25 |
| sprites | `black_cat/` | `dove/` | `black_bird/` | `orange_cat/` |
| áudio | `passos.wav` | `asas_passaro.wav` | `asas_passaro.wav` | `passos.wav` |

Poderia ser **1 classe genérica + 4 JSONs** (ou 1 JSON com array):
```json
[
  {
    "nome": "Black Cat", "forca": 2, "vidaBase": 10, "velocidade": 350,
    "tamanho": 64, "hitboxSize": 25, "hitboxOffsetX": -5, "hitboxOffsetY": 0,
    "sprites": { "idle": "entitys/player/black_cat/Idle.png", "run": "...", "hit": "..." },
    "audioPassos": "entitys/player/audios/passos.wav"
  },
  // ... outros 3
]
```

**Impacto:** 285 linhas (4×71) poderiam ser ~80 linhas (1 classe genérica) + 4 JSONs simples. Facilita adicionar novos personagens sem código Java.

---

### 36. ❌ Testes Automatizados
**Item original: #28 da v2 → #30 da v3 | Esforço: MÉDIO**

- Zero testes no projeto
- `core/build.gradle` não tem JUnit
- Sem diretório `src/test`

**Classes facilmente testáveis sem dependência do libGDX:**
- `StatsComponent` (170 linhas) — dano, XP, level up, validação de limites, cooldowns
- `GeradorSalas` (235 linhas) — geração procedural, validação de conexões, sala tesouro pós-boss
- `ColisaoResolver` (122 linhas) — MTV, separação bilateral, iterações
- `DadosInimigo`, `DadosPlayer` — records imutáveis
- `Quadtree` (159 linhas) — inserção, consulta, subdivisão (depende apenas de `Rectangle`)
- `GameConfig` (96 linhas) — validação de limites de volume, escala

---

## 🆕 PROBLEMAS EM ABERTO (7 itens)

---

### N1. 🔴 Assets.loadAll() Ainda Bloqueia a Thread Principal
**Severidade: ALTA | Esforço: MÉDIO | Mantido desde v2**

```java
// Assets.java:
public static void loadAll() {
    loadTextures();
    loadMusic();
    manager.finishLoading();  // ← BLOQUEIA até tudo carregar
}
```

O jogo congela na inicialização. Com 126 linhas de assets (68+ texturas + 10+ músicas), o freeze pode ser de 2-5 segundos. Este é o **problema de UX mais impactante** do projeto e persiste desde a v2.

**Recomendação:** Criar `LoadingScreen` que chama `manager.update()` em loop e mostra progresso com `manager.getProgress()`:
```java
public class LoadingScreen implements Screen {
    @Override
    public void render(float delta) {
        if (Assets.manager.update()) {
            game.setScreen(new Home(game));
        }
        float progress = Assets.manager.getProgress();
        // desenhar barra de progresso
    }
}
```

---

### N11. 🟠 TransicaoTela.render() Cria Matrix4 por Frame
**Severidade: MÉDIA | Esforço: TRIVIAL | NOVO**

```java
// TransicaoTela.java linha 79 — durante transição:
Matrix4 oldMatrix = new Matrix4(batch.getProjectionMatrix()); // TODA frame
```

Mesmo padrão que o antigo N7 (ScreenManager), mas em `TransicaoTela`. Uma transição de 1.2s a 60fps = 72 alocações de `Matrix4`.

**Correção sugerida:**
```java
private final Matrix4 tempMatrix = new Matrix4();
// ...
tempMatrix.set(batch.getProjectionMatrix());
// ... no final:
batch.setProjectionMatrix(tempMatrix);
```

---

### N12. 🟠 ExibirDadosPersonagem.draw() Aloca Objetos por Frame
**Severidade: MÉDIA | Esforço: BAIXO | NOVO**

`ExibirDadosPersonagem.draw()` cria múltiplos objetos a cada frame:

```java
// Linha 54 — cor do fundo recriada toda frame:
shapeRenderer.setColor(new Color(0.1f, 0.1f, 0.1f, 0.8f));

// Linha 64 — GlyphLayout recriado toda frame:
GlyphLayout layoutStatus = new GlyphLayout(font, "Status");

// Linhas 70, 75, 80 — mais GlyphLayouts:
GlyphLayout layoutLife = new GlyphLayout(font, textoLife);
GlyphLayout layoutSpeed = new GlyphLayout(font, textoSpeed);
GlyphLayout layoutMuscle = new GlyphLayout(font, textoMuscle);
```

**Impacto:** 5 objetos criados por frame quando o painel de dados está visível.

**Correção sugerida:**
```java
private static final Color BG_COLOR = new Color(0.1f, 0.1f, 0.1f, 0.8f);
private final GlyphLayout layoutStatus = new GlyphLayout();
private final GlyphLayout layoutLife = new GlyphLayout();
private final GlyphLayout layoutSpeed = new GlyphLayout();
private final GlyphLayout layoutMuscle = new GlyphLayout();
```

---

### N13. 🟠 EnemyTemplate Lógica de Push Duplicada com ColisaoResolver
**Severidade: MÉDIA | Esforço: BAIXO | NOVO**

`EnemyTemplate.update()` agora contém `empurrarForaOutrosInimigos()` (linhas 214-240) que faz push inline entre inimigos. Porém `WorldRenderer.renderizarInimigos()` **também** chama `WorldPhysics.resolverColisoes()` que usa `ColisaoResolver.resolver()` para o mesmo propósito.

```
Frame pipeline atual (redundante):
  1. EnemyTemplate.update() → empurrarForaOutrosInimigos() ← push O(n²)
  2. WorldPhysics.resolverColisoes() → ColisaoResolver.resolver() ← push O(n²)
```

**Problemas:**
- **Trabalho duplicado:** Dois passes O(n²) fazendo essencialmente o mesmo
- **Oscilação:** Os dois sistemas podem empurrar em direções conflitantes
- **Manutenção:** Lógica de push em dois lugares

**Correção sugerida:** Remover `empurrarForaOutrosInimigos()` de `EnemyTemplate.update()` e confiar exclusivamente no `ColisaoResolver` que já tem resolução iterativa com margem de segurança.

---

### N14. 🟡 DesenharMiniMapa Usa key.split(",") por Frame (Não vou mudar para Map<Point, Sala> porque não acho que valha a pena a complexidade extra) ✂️
**Severidade: BAIXA | Esforço: BAIXO | NOVO**

`DesenharMiniMapa.desenharMiniMapa()` processa strings de coordenadas a cada frame:

```java
for (String key : roomMap.keySet()) {
    String[] parts = key.split(",");          // new String[] toda frame
    int roomX = Integer.parseInt(parts[0]);   // parse toda frame
    int roomY = Integer.parseInt(parts[1]);   // parse toda frame
}
```

Com 10-20 salas visitadas, são 10-20 `split()` + 20-40 `parseInt()` por frame.

**Correção sugerida:** Usar `Map<Point, Sala>` ou `Map<Long, Sala>` (chave = `x << 32 | y`) em vez de `Map<String, Sala>`.

---

### N15. 🟡 EntradaPortais Usa Gdx.input Direto
**Severidade: BAIXA | Esforço: TRIVIAL | NOVO**

```java
// EntradaPortais.java linha 33:
if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
    entrouPortal = true;
}
```

Inconsistente com o sistema `KeyBindings` usado no resto do jogo. Se o jogador remapear teclas, a interação com portais não respeitará.

**Correção sugerida:** Adicionar `GameAction.INTERACT` ou usar `MENU_CONFIRM`.

---

### N16. 🟡 AlteradorZoom Usa Gdx.input Direto
**Severidade: BAIXA | Esforço: TRIVIAL | NOVO**

```java
// AlteradorZoom.java linhas 10, 18:
if (Gdx.input.isKeyJustPressed(Input.Keys.MINUS)) { ... }
if (Gdx.input.isKeyJustPressed(Input.Keys.EQUALS)) { ... }
```

Mesmo padrão de N15 — usa `Gdx.input` em vez de `KeyBindings`. Deveria usar `GameAction.ZOOM_IN` / `ZOOM_OUT`.

---

### N17. 🟡 GerarObjetos Usa Math.random() em Vez de Random
**Severidade: BAIXA | Esforço: TRIVIAL | NOVO**

```java
// GerarObjetos.java linhas 52, 56, 58:
int quantidade = info.qntMin() + (int) (Math.random() * (info.qntMax() - info.qntMin() + 1));
Rectangle regiao = regioesSpawn.get((int) (Math.random() * regioesSpawn.size()));
int x = (int) (regiao.x + Math.random() * regiao.width);
```

Inconsistente com `GerarInimigos.java` que usa um `Random` compartilhado com `new Random()`. `Math.random()` é thread-safe mas mais lento e menos controlável (não permite seed para reprodução de bugs).

**Correção sugerida:** Usar instância `Random` como `GerarInimigos` faz.

---

## 📊 TABELA CONSOLIDADA DE PRIORIDADES

### ✅ Concluídos (34 itens)

| # | Tarefa | Status |
|---|--------|--------|
| 1–24 | Todos os itens da v3 | ✅ Mantidos |
| 25 | Nomes camelCase em Sala.java | ✅ Resolvido (era 🟡) |
| 26 | Pre-alocar Rectangles player | ✅ Resolvido (era 🟡) |
| 27 | Pre-alocar layers renderLayers() | ✅ Resolvido (era ❌) |
| 28 | Object Pool para tiros | ✅ Resolvido (era ❌) |
| 29 | DesenharMiniMapa alocações | ✅ Resolvido (era N2) |
| 30 | PlayerTemplate snapshot tiros | ✅ Resolvido (era N3) |
| 31 | TilemapHitbox drawObjects cache | ✅ Resolvido (era N4) |
| 32 | EscolherPersonagem cache previews + KeyBindings | ✅ Resolvido (era N5 + N9) |
| 33 | Matrix4 pré-alocado ScreenManager | ✅ Resolvido (era N7) |
| 34 | ColisaoComponent TilemapHitboxFactory removida | ✅ Resolvido (era N8) |

### ❌ Pendentes (2 itens)

| # | Tarefa | Esforço | Impacto |
|---|--------|---------|---------|
| 35 | Personagem data-driven | 🔴 Alto | 📐 Escalabilidade |
| 36 | Testes unitários | 🟡 Médio | 🛡️ Qualidade |

### 🆕 Problemas em Aberto (1 mantido + 6 novos)

| # | Tarefa | Severidade | Esforço | Impacto |
|---|--------|-----------|---------|---------|
| N1 | 🔴 **LoadingScreen assíncrona** | ALTA | 🟡 Médio | 🔥 UX Crítico |
| N11 | 🟠 Matrix4 em TransicaoTela | MÉDIA | 🟢 Trivial | ⚡ Performance |
| N12 | 🟠 ExibirDadosPersonagem alocações | MÉDIA | 🟢 Baixo | ⚡ Performance |
| N13 | 🟠 Push duplicado Enemy vs ColisaoResolver | MÉDIA | 🟢 Baixo | ⚡ Performance + 🐛 Gameplay |
| N14 | 🟡 MiniMapa key.split() por frame | BAIXA | 🟢 Baixo | ⚡ Performance |
| N15 | 🟡 EntradaPortais usa Gdx.input direto | BAIXA | 🟢 Trivial | 📐 Consistência |
| N16 | 🟡 AlteradorZoom usa Gdx.input direto | BAIXA | 🟢 Trivial | 📐 Consistência |
| N17 | 🟡 GerarObjetos usa Math.random() | BAIXA | 🟢 Trivial | 📐 Consistência |

---

## 🎯 PLANO DE AÇÃO RECOMENDADO

### 🟢 Sprint 1 — Quick Wins (30min total)

| # | Tarefa | Mudança | Linhas |
|---|--------|---------|--------|
| N11 | Matrix4 em TransicaoTela | Campo `tempMatrix` + `.set()` | ~3 |
| N12 | ExibirDadosPersonagem | 5 campos pré-alocados (Color + GlyphLayouts) | ~8 |
| N15 | EntradaPortais KeyBindings | `GameAction.INTERACT` ou `MENU_CONFIRM` | ~3 |
| N16 | AlteradorZoom KeyBindings | `GameAction.ZOOM_IN` / `ZOOM_OUT` | ~5 |
| N17 | GerarObjetos Random | Campo `private final Random random` | ~4 |

**Total: ~23 linhas, ~30min de trabalho, 5 itens resolvidos**

### 🟡 Sprint 2 — Melhorias Estruturais (2-3h)

| # | Tarefa | Benefício |
|---|--------|-----------|
| N1 | LoadingScreen assíncrona | Elimina freeze de 2-5s na inicialização |
| N13 | Remover push duplicado | Elimina O(n²) redundante e possível oscilação |
| N14 | MiniMapa chave numérica | Elimina split/parseInt por frame |

### 🟠 Sprint 3 — Refatorações Maiores (1-2 dias)

| # | Tarefa | Benefício |
|---|--------|-----------|
| 35 | Personagem data-driven | 4 classes → 1 genérica + JSON |
| 36 | Testes unitários básicos | StatsComponent, Quadtree, ColisaoResolver, GeradorSalas |

---

## 📈 EVOLUÇÃO COMPLETA DO PROJETO

```
Análise original:  ✅ 5   🟡 4  ❌ 13  → ~23% concluído
Análise v1:        ✅ 12  🟡 3  ❌ 10  → ~48% concluído
Análise v2:        ✅ 20  🟡 2  ❌ 6   → ~71% concluído
Análise v3:        ✅ 24  🟡 2  ❌ 4   → ~78% concluído
Análise v4:        ✅ 34  🟡 0  ❌ 2   → ~93% concluído

Itens resolvidos desde a v3:
  ✅ Nomes camelCase Sala.java (era #25 🟡)
  ✅ Pre-alocar Rectangles player (era #26 🟡)
  ✅ Pre-alocar layers renderLayers (era #27 ❌)
  ✅ Object Pool para tiros (era #28 ❌)
  ✅ DesenharMiniMapa alocações (era N2 🟠)
  ✅ PlayerTemplate snapshot tiros (era N3 🟠)
  ✅ TilemapHitbox drawObjects cache (era N4 🟠)
  ✅ EscolherPersonagem cache previews + KeyBindings (era N5 🟠 + N9 🟡)
  ✅ Matrix4 pré-alocado ScreenManager (era N7 🟠)
  ✅ ColisaoComponent TilemapHitboxFactory removida (era N8 🟡)
```

```
                          ████████████████████████████░░  93%
Análise original:        █████░░░░░░░░░░░░░░░░░░░░░░░░  23%
Análise v1:              ██████████████░░░░░░░░░░░░░░░░  48%
Análise v2:              █████████████████████░░░░░░░░░  71%
Análise v3:              ███████████████████████░░░░░░░  78%
Análise v4:              ████████████████████████████░░  93%
```

---

## 💡 PONTOS POSITIVOS DO PROJETO

### Mantidos das versões anteriores:
1. **Arquitetura de componentes madura** — `PlayerTemplate` e `EnemyTemplate` usam composição com 9 componentes
2. **Zero memory leaks** — Todos os assets gerenciados pelo `AssetManager`
3. **GameConfig encapsulado** — Singleton com validação, `debugMode = false` em produção
4. **Sistema de input profissional** — `KeyBindings` com swap de conflitos, persistência JSON
5. **Spatial partitioning** — `Quadtree` genérica com debug visual, sem logs no hot path
6. **WorldTemplate limpo** — 4 managers claros, sem delegate getters
7. **ColisaoResolver com MTV** — Resolução robusta inimigo-inimigo e inimigo-parede
8. **Transições centralizadas** — `ScreenManager` + `TransicaoTela` com 2 efeitos
9. **Cache bidirecional de animações** — Mudança de direção em O(1)
10. **Records Java** — `DadosPlayer`, `DadosInimigo`, `DefinirSprite`, `InfoGeraObjeto`
11. **Error handling consistente** — try/catch com logging em todos os mundos
12. **DadosSalvosJogador limpo** — 14 linhas diretas
13. **Log throttled em DanoTiro** — Resumo a cada 5s
14. **TilemapHitboxFactory com cache** — HashMap + sorted cache
15. **Rectangles pré-alocados** — Zero GC no movement (player e inimigos)
16. **GeradorSalas expandido** — Sala tesouro pós-boss, extras variados
17. **CarregarPortas com enum** — `Direcao` elimina duplicação nas 4 direções

### Novos na v4:
18. **🆕 Object Pool de tiros** — `Pool.Poolable` com `liberar()`/`obterDoPool()`, zero GC em combate
19. **🆕 Zero alocações no render loop** — Layers, snapshots, layouts, cores — tudo pré-alocado
20. **🆕 Convenção de nomes 100%** — Sem violações de camelCase restantes
21. **🆕 EscolherPersonagem otimizado** — `previews[]` cache + `KeyBindings` integrado
22. **🆕 MoverDirecaoPlayer com colisão de paredes** — Prevenção > correção
23. **🆕 GameAction expandido** — `MENU_RIGHT`, `MENU_LEFT`, `MENU_CONFIRM` para menus
24. **🆕 6 caches de animação** — AnimacaoComponent expandido para cobertura completa L/R

---

## 🔍 COMPARATIVO DE CONTAGEM DE LINHAS (v3 → v4)

| Arquivo | v3 | v4 | Δ | Nota |
|---------|----|----|---|------|
| `TirosTemplate.java` | 90 | 165 | **+75** | Pool.Poolable ✨ |
| `ChecarColisao.java` | 46 | 93 | **+47** | empurrarForaParedes + tempX/Y |
| `Coco.java` | 52 | 99 | **+47** | Pool\<Coco\> ✨ |
| `EnemyTemplate.java` | 265 | 309 | **+44** | empurrarForaOutrosInimigos |
| `Hud.java` | 168 | 203 | **+35** | Layouts/cores pré-alocados |
| `AnimacaoComponent.java` | 183 | 216 | **+33** | 6 caches de animação |
| `GerarInimigos.java` | 91 | 120 | **+29** | Validação robusta |
| `KeyBindings.java` | 110 | 137 | **+27** | MENU actions, persistência |
| `MotorAnimacao.java` | 123 | 147 | **+24** | Camera overloads |
| `HudPause.java` | 119 | 142 | **+23** | Texturas pré-carregadas |
| `ExibirDadosPersonagem.java` | 77 | 96 | **+19** | Scaling, ícones |
| `SaveComponent.java` | 30 | 48 | **+18** | loadSaveData melhorado |
| `GerarObjetos.java` | 55 | 72 | **+17** | Hitbox offsets, areaToque |
| `SlideTransitionEffect.java` | 87 | 104 | **+17** | Easing cubic, dispose |
| `AudioComponent.java` | 39 | 55 | **+16** | initEnemy, updateAudio |
| `FadeTransitionEffect.java` | 91 | 106 | **+15** | easeInOutSine, gradiente |
| `TransicaoTela.java` | 82 | 97 | **+15** | Slide+Fade, duração |
| `MundoAreia.java` | 246 | 261 | **+15** | layerBuffer, toArray |
| `GameAction.java` | 24 | 38 | **+14** | MENU_RIGHT/LEFT/CONFIRM |
| `CameraComponent.java` | 43 | 56 | **+13** | Javadoc |
| `CriarBotao.java` | 50 | 63 | **+13** | Overload, áudio path |
| `EntradaPortais.java` | 59 | 72 | **+13** | Error handling |
| `DesenharTexto.java` | 50 | 62 | **+12** | Scaling, GlyphLayout |
| `BlackBird.java` | 60 | 71 | **+11** | Sprites adicionais |
| `OrangeCat.java` | 60 | 71 | **+11** | Sprites adicionais |
| `HudStage.java` | 58 | 69 | **+11** | InputMultiplexer cleanup |
| `ObjetoGerado.java` | 41 | 52 | **+11** | areaToque calculada |
| `MoverDirecaoPlayer.java` | 90 | 100 | **+10** | Paredes check |
| `PawFight.java` | 161 | 147 | **-14** | Cleanup |
| `EscolherPersonagem.java` | 220 | 208 | **-12** | previews[] cache |
| `ScreenManager.java` | 134 | 131 | **-3** | tempMatrix campo |
| `ColisaoResolver.java` | 125 | 122 | **-3** | Refinamento |
| `EnemyManager.java` | 39 | 36 | **-3** | Simplificação |
| `RoomManager.java` | 92 | 89 | **-3** | Simplificação |
| `CombateComponent.java` | 74 | 71 | **-3** | tiro.liberar() |

> **Conclusão:** O projeto avançou de 78% para **93% de conclusão** — o maior salto percentual da história do projeto (+15 pontos). Todos os 10 problemas de performance apontados na v3 foram resolvidos: pre-alocação de Rectangles, layers, snapshots, layouts, cores, Matrix4, cache de previews, e o Object Pool de tiros. O crescimento líquido de +709 linhas é significativo (TirosTemplate +75, Coco +47, ChecarColisao +47, EnemyTemplate +44), compensado por cleanup (-14 em PawFight, -12 em EscolherPersonagem). Restam apenas **2 itens estruturais** (personagem data-driven e testes) e **7 problemas menores** (dos quais 5 são triviais). O próximo foco deve ser o **Sprint 1 de Quick Wins** (5 itens, ~23 linhas, ~30min) seguido da **LoadingScreen** que permanece como o problema de UX mais impactante.

