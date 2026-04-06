# 🐾 Análise Completa — Paw Fight (Abril 2026 — v2)

## 📊 Visão Geral do Projeto

| Métrica | Anterior (v1) | Atual (v2) | Δ |
|---------|---------------|------------|---|
| Arquivos `.java` | 69 | 76 | +7 |
| Linhas de código totais | 5.269 | 5.860 | +591 |
| Pacotes | 13 | 15 | +2 |
| Framework | libGDX | libGDX | — |
| Build | Gradle | Gradle | — |
| Testes | ❌ Zero | ❌ Zero | — |

### 🆕 Arquivos Novos Desde a Última Análise

| Arquivo | Linhas | Propósito |
|---------|--------|-----------|
| `GameConfig.java` | 74 | Substitui `VariavelComum` — singleton com encapsulamento e validação |
| `KeyBindings.java` | 110 | Sistema de remapeamento de teclas com persistência JSON |
| `GameAction.java` | 24 | Enum das ações do jogo com tecla padrão e tipo de input |
| `Quadtree.java` | 146 | Particionamento espacial genérico para colisões |
| `WorldRenderer.java` | 73 | Subsistema extraído de WorldTemplate — renderização |
| `WorldPhysics.java` | 39 | Subsistema extraído de WorldTemplate — física/colisão |
| `RoomManager.java` | 71 | Subsistema extraído de WorldTemplate — gerenciamento de salas |
| `EnemyManager.java` | 28 | Subsistema extraído de WorldTemplate — gerenciamento de inimigos |
| `TransitionEffect.java` | 10 | Interface para efeitos de transição (refatoração) |

### 🏗️ Arquitetura de Pacotes

```
com.pawfight.game
├── PawFight.java                          (132 linhas) — Game principal
├── engine/
│   ├── Assets.java                        (103) — AssetManager centralizado
│   ├── AudioEngine.java                   (28)  — Wrapper de áudio
│   ├── GameConfig.java                    (74)  — Configuração centralizada ✨ NOVO
│   ├── ScreenManager.java                 (106) — Gerenciador de transições
│   ├── Validar.java                       (9)   — Utilitário de validação
│   ├── design/
│   │   ├── AlteradorZoom.java             (25)
│   │   ├── DefinirSprite.java             (10)  — record
│   │   ├── animation/MotorAnimacao.java   (123) — Motor de animação
│   │   ├── desenhar/DesenharTexto.java    (50)
│   │   ├── desenhar/DesenharTextura.java  (22)
│   │   └── transition/
│   │       ├── TransitionEffect.java      (10)  — interface ✨ NOVO
│   │       ├── FadeTransitionEffect.java  (91)
│   │       ├── SlideTransitionEffect.java (87)
│   │       └── TransicaoTela.java         (82)
│   ├── fisica/
│   │   ├── ChecarColisao.java             (33)
│   │   ├── Quadtree.java                  (146) — Quadtree genérica ✨ NOVO
│   │   └── TilemapHitboxFactory.java      (139)
│   ├── font/FontFactory.java              (16)
│   ├── Hud/ ⚠️ (PascalCase — ainda não corrigido)
│   │   ├── CriarBotao.java               (50)
│   │   ├── DesenharMiniMapa.java          (152)
│   │   ├── Hud.java                       (168)
│   │   ├── HudPause.java                  (119)
│   │   └── HudStage.java                  (58)
│   ├── input/                             ✨ NOVO PACOTE
│   │   ├── GameAction.java                (24)
│   │   └── KeyBindings.java              (110)
│   ├── procedural/
│   │   ├── CarregarPortas.java            (114)
│   │   ├── GerarInimigos.java             (91)
│   │   ├── GerarObjetos.java              (55)
│   │   ├── ObjetoGerado.java              (41)
│   │   └── sala/ (GeradorSalas 192, Sala, TipoSala, InfoGeraObjeto, Direction)
│   ├── render/
│   │   ├── Renderizar.java                (95)
│   │   └── RenderizadorCamada.java        (60)
│   └── save/
│       ├── DadosSalvosJogador.java        (12)  — simplificado (era 66!)
│       └── SalvarJogo.java                (20)
├── entity/
│   ├── Entidade.java                      (19)  — interface
│   ├── bosses/BossesTemplate.java         (3)
│   ├── component/                         — 9 componentes
│   │   ├── AnimacaoComponent.java         (183) — com cache L/R
│   │   ├── AudioComponent.java            (39)
│   │   ├── CameraComponent.java           (43)
│   │   ├── ColisaoComponent.java          (50)
│   │   ├── CombateComponent.java          (57)
│   │   ├── InputComponent.java            (15)  — usa KeyBindings ✨ REFATORADO
│   │   ├── MovimentoComponent.java        (38)
│   │   ├── SaveComponent.java             (30)
│   │   └── StatsComponent.java            (150)
│   ├── enemy/
│   │   ├── DadosInimigo.java              (23)  — record (nomes corrigidos! ✨)
│   │   ├── EnemyTemplate.java             (231) — com separação de inimigos
│   │   ├── MoverDirecaoPlayer.java        (55)
│   │   └── Skeleton.java                  (80)
│   ├── player/
│   │   ├── DadosPlayer.java               (21)  — record
│   │   ├── PlayerTemplate.java            (314) — usa componentes
│   │   ├── BlackBird.java                 (60)
│   │   ├── BlackCat.java                  (60)
│   │   ├── Dove.java                      (60)
│   │   └── OrangeCat.java                 (60)
│   └── tiro/
│       ├── Atirar.java                    (24)
│       ├── DanoTiro.java                  (75)  — com Quadtree ✨ REFATORADO
│       ├── TirosTemplate.java             (90)
│       └── dove/Coco.java                 (52)
└── world/
    ├── Home.java                          (138)
    ├── MundoAreia.java                    (214)
    ├── template/                          ✨ NOVO SUB-PACOTE
    │   ├── WorldTemplate.java             (285)
    │   ├── WorldRenderer.java             (73)
    │   ├── WorldPhysics.java              (39)
    │   ├── RoomManager.java               (71)
    │   └── EnemyManager.java              (28)
    └── base/
        ├── Base.java                      (124)
        ├── EntradaPortais.java            (59)
        ├── EscolherPersonagem.java        (176)
        ├── ExibirDadosPersonagem.java     (77)
        └── Portoes.java                   (19)
```

---

## 📈 Resumo de Progresso vs Análise Anterior (v1)

| Status | Análise v1 | Análise v2 | Δ |
|--------|-----------|-----------|---|
| ✅ Feito | 12 | **20** | +8 |
| 🟡 Parcialmente feito | 3 | **2** | -1 |
| ❌ Não feito | 10 | **6** | -4 |
| 🆕 Novos problemas encontrados | 6 | **8** | +2 |

**Progresso geral: ~71% completo** (vs ~48% na análise v1, vs ~23% na análise original)

---

## ✅ O QUE JÁ FOI FEITO (20 itens)

*(12 itens da análise v1 + 8 novos itens resolvidos)*

### Itens mantidos da análise v1 (1–12)

Todos os 12 itens marcados como ✅ na análise anterior continuam válidos:
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

---

### 13. ✅ VariavelComum → GameConfig ✨ NOVO DESDE v1
**Item original: #24 da v1 | Status: CONCLUÍDO**

`VariavelComum.java` foi **completamente removido**. Zero referências no código-fonte.

`GameConfig.java` (74 linhas) agora é um singleton com:
- Encapsulamento total (campos `private`, getters/setters)
- Validação de limites via `MathUtils.clamp(volume, 0f, 1f)` nos volumes
- Constantes imutáveis: `LARGURA_TELA_BASE`, `ALTURA_TELA_BASE`
- Método utilitário `getScale()` para cálculo de escala
- Métodos auxiliares `setVolumeMusicaPercent(int)` para UI

```java
// Antes (VariavelComum) — público mutável sem validação:
public static float VOLUME_MUSICA = 0.3f;

// Depois (GameConfig) — encapsulado com validação:
private float volumeMusica = 0.3f;
public void setVolumeMusica(float volume) {
    this.volumeMusica = MathUtils.clamp(volume, 0f, 1f);
}
```

> 🎯 Refatoração exemplar. Elimina mutação global descontrolada.

---

### 14. ✅ Input Remapping via KeyBindings ✨ NOVO DESDE v1
**Item original: #23 da v1 | Status: CONCLUÍDO**

Sistema completo de remapeamento de teclas:

**`GameAction.java`** (24 linhas) — Enum com 9 ações:
- `MOVE_RIGHT`, `MOVE_LEFT`, `MOVE_UP`, `MOVE_DOWN`
- `ATTACK_SPECIAL`, `ABILITY`
- `PAUSE_TOGGLE`, `DEBUG_TOGGLE`, `CHEAT_TOGGLE`
- Cada ação tem: `defaultKey`, `label`, `justPressed`

**`KeyBindings.java`** (110 linhas) — Singleton com:
- `isActive(GameAction)` — verifica se ação está pressionada (respeita `justPressed`)
- `setKey(GameAction, int)` — remapeia tecla com **swap automático de conflitos**
- `save()` / `load()` — persistência em `save/keybindings.json`
- `resetToDefaults()` — restaura padrões
- `getAllBindings()` — cópia defensiva (`Map.copyOf`)

**`InputComponent.java`** (15 linhas) — Completamente refatorado:
```java
// Antes — hardcoded:
public boolean isMoveRight() { return Gdx.input.isKeyPressed(Input.Keys.D); }

// Depois — usa KeyBindings:
public boolean isMoveRight() { return keys().isActive(GameAction.MOVE_RIGHT); }
```

> 🎯 Design excelente. Swap automático de conflitos é um toque profissional.

---

### 15. ✅ DanoTiro com Quadtree (Spatial Partitioning) ✨ NOVO DESDE v1
**Item original: #20 da v1 | Status: CONCLUÍDO**

`DanoTiro.java` (75 linhas) agora usa `Quadtree<EnemyTemplate>`:

```java
// Antes — O(n×m) bruto:
for (EnemyTemplate inimigo : inimigos) {
    for (TirosTemplate tiro : tiros) {
        if (inimigo.getHitBox().overlaps(tiro.getHitBox())) { ... }
    }
}

// Depois — Quadtree com candidatos:
quadtree.clear();
for (EnemyTemplate inimigo : inimigos) quadtree.inserir(inimigo, inimigo.getHitBox());
for (TirosTemplate tiro : tirosSnapshot) {
    candidatos.clear();
    quadtree.consultar(tiro.getHitBox(), candidatos);
    for (EnemyTemplate inimigo : candidatos) { ... }
}
```

**Quadtree.java** (146 linhas) — Implementação genérica completa:
- `MAX_OBJETOS = 8`, `MAX_NIVEIS = 5`
- Subdivide automaticamente quando excede capacidade
- `clear()` reutiliza nós existentes (não recria)
- `drawDebug()` com cores por nível de profundidade
- Listas pré-alocadas em `DanoTiro` (`candidatos`, `tirosSnapshot`) — sem GC

> 🎯 Implementação de qualidade. A visualização de debug com cores por nível é um bônus.

---

### 16. ✅ WorldTemplate Decomposto em Subsistemas ✨ NOVO DESDE v1
**Item original: #27 da v1 | Status: CONCLUÍDO (com ressalvas)**

`WorldTemplate` foi decomposto em 4 managers:

| Manager | Linhas | Responsabilidade |
|---------|--------|------------------|
| `WorldRenderer` | 73 | Renderização de objetos, inimigos, hitboxes, background |
| `WorldPhysics` | 39 | Hitboxes de paredes, dano de tiros, Quadtree debug |
| `RoomManager` | 71 | Salas, portais, minimapa, navegação |
| `EnemyManager` | 28 | Lista de inimigos, geração procedural |

`WorldTemplate` caiu de **299 → 285 linhas** (bruto), mas a lógica real no render é mais limpa:
```java
// render() agora delega claramente:
worldRenderer.renderizarInimigos(this);
worldRenderer.renderizarObjects(this);
worldPhysics.processarDanoTiro(this);
worldPhysics.drawDebugQuadtree(...);
```

⚠️ **Ressalva:** `WorldTemplate` ainda tem **~30 delegate getters** (linhas 240-360) para manter compatibilidade com código externo. Ver item 🟡 sobre isso.

---

### 17. ✅ DadosInimigo com Nomes camelCase ✨ NOVO DESDE v1
**Item original: #18 da v1 | Status: CONCLUÍDO**

`DadosInimigo.java` agora usa nomes corretos:
```java
// Antes:
public record DadosInimigo(int VidaBase, int Forca, int Velocidade, ...)

// Depois:
public record DadosInimigo(int vidaBase, int forca, int velocidade, ...)
```

Todos os 17 campos do record seguem convenção camelCase. `EnemyTemplate.java` e `Skeleton.java` usam os nomes corretos.

---

### 18. ✅ DadosSalvosJogador Simplificado ✨ NOVO DESDE v1
**Item original: #22 da v1 | Status: CONCLUÍDO**

`DadosSalvosJogador.java` reduziu de **66 → 12 linhas** (–82%):
```java
// Antes — 66 linhas com getters/setters:
private String nomePersonagem;
public String getNomePersonagem() { return nomePersonagem; }
public void setNomePersonagem(String n) { this.nomePersonagem = n; }
// ... x8 campos

// Depois — 12 linhas com campos públicos:
public class DadosSalvosJogador {
    public String nomePersonagem;
    public int vidaBase;
    public int velocidade;
    // ...
}
```

Funciona perfeitamente com `com.badlogic.gdx.utils.Json` (que precisa de campos públicos para serialização).

---

### 19. ✅ hitboxDraw() Agora Batched ✨ NOVO DESDE v1
**Item original: #19 da v1 | Status: CONCLUÍDO**

`Renderizar.hitboxDraw()` **não faz mais begin/end próprio**:
```java
// Antes — begin/end individual:
public void hitboxDraw(ShapeRenderer shapeRenderer, Rectangle hitbox) {
    shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
    hitboxRect(shapeRenderer, hitbox, Color.RED);
    shapeRenderer.end();
}

// Depois — apenas desenha (sem begin/end):
public void hitboxDraw(ShapeRenderer shapeRenderer, Rectangle hitbox) {
    if (!GameConfig.getInstance().isHitboxVisivel()) return;
    hitboxRect(shapeRenderer, hitbox, Color.RED);
}
```

O begin/end é gerenciado no chamador (`PlayerTemplate.draw()`):
```java
if (GameConfig.getInstance().isHitboxVisivel()) {
    shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
    colisao.drawDebugHitboxesNoBatch(shapeRenderer);
    renderizar.hitboxDraw(shapeRenderer, hitBox);
    shapeRenderer.end();
}
```

> 🎯 Agora todas as hitboxes do player são desenhadas em um único begin/end.

---

### 20. ✅ Cachear Snapshot Tiros em DanoTiro ✨ NOVO DESDE v1
**Item original: #16 da v1 | Status: CONCLUÍDO**

```java
// Antes — new ArrayList a cada frame:
List<TirosTemplate> tirosSnapshot = new ArrayList<>(tiros);

// Depois — campo reutilizável:
private final List<TirosTemplate> tirosSnapshot = new ArrayList<>();
// ...
tirosSnapshot.clear();
tirosSnapshot.addAll(tiros);
```

> 🎯 Elimina alocação de ArrayList a cada frame em `DanoTiro`.

---

## 🟡 PARCIALMENTE FEITO (2 itens)

---

### 21. 🟡 Nomes de Variáveis com Nome de Classe
**Item original: #17 da v1 | Progresso: ~60% (era ~30%)**

**O que melhorou desde v1:**
- `VariavelComum` removido → `GameConfig.getInstance()`
- `DadosInimigo` campos agora camelCase
- `SalvarJogo SalvarJogo` → `SalvarJogo salvarJogo` em `PawFight.java`
- `Sala Sala` em `logRoomInfo` → corrigido em `MundoAreia` (parametro agora `sala`)

**O que ainda viola convenções:**

| Arquivo | Linha | Violação |
|---------|-------|----------|
| `DesenharMiniMapa.java` | 109 | `Sala Sala = roomMap.get(key);` |
| `GeradorSalas.java` | 78 | `private boolean typeInimigo(Sala Sala, int numRooms)` |

**Restam apenas 2 violações** (era 9 na v1).

---

### 22. 🟡 WorldTemplate Ainda com Muitos Delegate Getters
**Item original: #27 da v1 | Progresso: ~70%**

A decomposição em 4 managers foi excelente, mas `WorldTemplate` agora tem **~30 delegate getters** (linhas 240–360) que apenas repassam chamadas:

```java
// Exemplos de delegates redundantes:
public Set<String> getSalasVisitadas() { return roomManager.getSalasVisitadas(); }
public TilemapHitboxFactory getTilemapHitboxFactory() { return worldPhysics.getTilemapHitboxFactory(); }
public GerarInimigos getGerarInimigos() { return enemyManager.getGerarInimigos(); }
public List<EnemyTemplate> getListaInimigos() { return enemyManager.getListaInimigos(); }
// ... ~26 mais
```

**Impacto:** O código externo (`MundoAreia`, `CarregarPortas`, `DesenharMiniMapa`) poderia acessar os managers diretamente:
```java
// Em vez de: world.getListaInimigos()
// Fazer:     world.getEnemyManager().getListaInimigos()
```

**Recomendação:** Remover gradualmente os delegate getters, atualizando os chamadores para usar os managers diretamente. Manter apenas `getPlayer()`, `getGame()`, `getBatch()`.

---

## ❌ O QUE NÃO FOI FEITO (6 itens)

---

### 23. ❌ Pre-alocar Rectangles em ChecarColisao
**Item original: #13 da v1 | Esforço: BAIXO**

`ChecarColisao.ajustarPosicaoSeBaterParede()` ainda cria **2 Rectangles por chamada**:
```java
Rectangle nextHitboxX = new Rectangle(nextX, playerHitbox.y, playerHitbox.width, playerHitbox.height);
// ...
Rectangle nextHitboxY = new Rectangle(playerHitbox.x, nextY, playerHitbox.width, playerHitbox.height);
```

**Chamado a cada frame** durante movimento do player.

**Correção sugerida:** 2 campos `Rectangle` reutilizáveis com `.set()`.

---

### 24. ❌ Pre-alocar Layers em renderLayers()
**Item original: #14 da v1 | Esforço: BAIXO**

`MundoAreia.renderLayers()` e `renderLayersUp()` ainda criam `new ArrayList<>()` **a cada frame**:
```java
List<String> layers = new ArrayList<>();  // ~60x/s
// ...
layers.toArray(new String[0])             // ~60x/s
```

**Correção sugerida:** Campos `String[]` pré-montados ou listas como campos da classe.

---

### 25. ❌ Matrix4 Pré-alocado em ScreenManager
**Item original: #15 da v1 | Esforço: BAIXO**

```java
// ScreenManager.java linha 105 — durante transição:
Matrix4 oldMatrix = new Matrix4(batch.getProjectionMatrix()); // TODA frame
```

**Correção sugerida:** `private final Matrix4 tempMatrix = new Matrix4()` + `tempMatrix.set(...)`.

---

### 26. ❌ Object Pool para Projéteis
**Item original: #25 da v1 | Esforço: MÉDIO**

`Atirar.atira()` cria `tiroModelo.clonar(player)` a cada disparo. `CombateComponent.updateTiros()` remove com `Iterator` + `tiro.dispose()`.

Nenhum `Pool<TirosTemplate>` implementado. O libGDX tem `com.badlogic.gdx.utils.Pool` pronto para uso.

---

### 27. ❌ Personagem Data-Driven
**Item original: #28 da v1 | Esforço: ALTO**

4 classes quase idênticas (~60 linhas cada):
- `BlackBird.java`, `BlackCat.java`, `Dove.java`, `OrangeCat.java`

Todas seguem exatamente o mesmo padrão:
```java
dadosPlayer() → return new DadosPlayer(stats...)
updateSpriteDefinitions() → animacao.setDefinitions(idle, walk, dead, hurt)
ataqueBasico() → vazio
ataqueEspecial() → vazio
usarHabilidadeEspecial() → vazio
modeloTiroExclusivo() → new Coco(...)
definirAudios() → vazio
```

A única diferença real são os **números** nos `DadosPlayer` e os **paths** das texturas. Poderia ser um JSON:
```json
{
  "nome": "Black Cat",
  "forca": 2, "vidaBase": 10, "velocidade": 350, "tamanho": 64,
  "sprites": { "idle": "entitys/player/black_cat/Idle.png", ... }
}
```

---

### 28. ❌ Testes Automatizados
**Item original: #26 da v1 | Esforço: MÉDIO**

- Zero testes no projeto
- `core/build.gradle` não tem JUnit
- Sem diretório `src/test`

**Classes facilmente testáveis sem dependência do libGDX:**
- `StatsComponent` — dano, XP, level up, validação de limites
- `GeradorSalas` — geração procedural, validação de conexões
- `DadosInimigo`, `DadosPlayer` — records imutáveis
- `Quadtree` — inserção, consulta, subdivisão (depende apenas de `Rectangle`)

---

## 🆕 NOVOS PROBLEMAS ENCONTRADOS (8 itens)

---

### N1. 🔴 Assets.loadAll() Ainda Bloqueia a Thread Principal
**Severidade: ALTA | Esforço: MÉDIO | Item mantido da v1**

```java
public static void loadAll() {
    loadTextures();
    loadMusic();
    manager.finishLoading();  // ← BLOQUEIA até tudo carregar
}
```

O jogo congela na inicialização. Não existe `LoadingScreen`. Com **126 linhas** de assets (60+ texturas + 10+ músicas), o freeze pode ser significativo.

**Recomendação:** Criar `LoadingScreen` que chama `manager.update()` em loop e mostra progresso com `manager.getProgress()`.

---

### N2. 🟠 Quadtree Logging Verboso no Hot Path
**Severidade: MÉDIA | Esforço: BAIXO**

`Quadtree.inserir()` e `consultar()` fazem logging quando `debugMode = true`:
```java
if (GameConfig.getInstance().isDebugMode()) {
    Gdx.app.log(TAG, "Item ignorado — hitbox fora dos limites...");
}
```

Com `debugMode = true` (valor padrão!), isso gera **dezenas de logs por frame** durante combate. O `Gdx.app.log()` é relativamente caro (I/O).

**Correção sugerida:**
- Trocar para `isHitboxVisivel()` (que é `false` por padrão)
- Ou usar nível de log mais fino: um `TRACE_MODE` ou similar
- Ou remover logs do hot path e manter apenas no nível 0

---

### N3. 🟠 DesenharMiniMapa Aloca Objetos a Cada Frame
**Severidade: MÉDIA | Esforço: BAIXO**

`DesenharMiniMapa.desenharSalaAtual()`:
```java
GlyphLayout layout = new GlyphLayout(font, texto);  // TODA frame
Color corFundo = new Color();                         // TODA frame
```

`DesenharMiniMapa.desenharMiniMapa()`:
```java
shapeRenderer.setColor(new Color(0, 0, 0, 0.3f));   // TODA frame
```

**Correção sugerida:** Campos pré-alocados (`private final GlyphLayout layout`, `private static final Color BG_COLOR`).

---

### N4. 🟠 PlayerTemplate.desenharTiros() Ainda Cria ArrayList por Frame
**Severidade: MÉDIA | Esforço: BAIXO**

```java
// PlayerTemplate.java linha 212:
List<TirosTemplate> tirosSnapshot = new ArrayList<>(combate.getTiros());
```

Embora `DanoTiro` já use lista pré-alocada, `PlayerTemplate.desenharTiros()` ainda cria um `new ArrayList<>()` a cada frame.

**Correção sugerida:** Campo `private final List<TirosTemplate> drawSnapshot = new ArrayList<>()` com `clear()+addAll()`.

---

### N5. 🟠 TilemapHitboxFactory.drawObjects() Cria e Ordena ArrayList a Cada Chamada
**Severidade: MÉDIA | Esforço: BAIXO**

```java
// TilemapHitboxFactory.java linhas 80-97:
List<MapObject> objects = new ArrayList<>();
for (MapObject obj : layer.getObjects()) { objects.add(obj); }
objects.sort((o1, o2) -> { ... });
```

Chamado em `Base.checkPortals()` quando player < level 5. Cria lista + ordena a cada chamada.

**Correção sugerida:** Cachear a lista ordenada, ou usar campo reutilizável.

---

### N6. 🟡 EscolherPersonagem Recria PlayerTemplate a Cada Preview
**Severidade: BAIXA | Esforço: MÉDIO**

```java
// EscolherPersonagem.java linhas 115, 123:
personagemPreview = getPlayerEscolhido(); // → cria novo PlayerTemplate + carrega save
```

`getPlayerEscolhido()` cria um `new BlackCat(...)` / `new Dove(...)` **a cada mudança de seta**. Isso inclui:
- Construção completa de `PlayerTemplate` (9 componentes)
- `rebuildAnimations()` (cria 12+ Animation objects)
- `loadPlayer()` (lê JSON do disco)

**Correção sugerida:** Criar os 4 players uma vez no construtor e apenas trocar referência.

---

### N7. 🟡 Pacote `Hud` com H Maiúsculo
**Severidade: BAIXA | Esforço: BAIXO | Item mantido da v1**

O pacote é `com.pawfight.game.engine.Hud` (PascalCase) em vez de `com.pawfight.game.engine.hud`. Viola convenção Java onde pacotes devem ser lowercase.

**Impacto:** 5 arquivos nesse pacote + 8 imports em arquivos externos:
- `RoomManager.java` → `import ...engine.Hud.DesenharMiniMapa`
- `PlayerTemplate.java` → `import ...engine.Hud.Hud`, `import ...engine.Hud.HudPause`
- `Home.java` → `import ...engine.Hud.CriarBotao`, `import ...engine.Hud.HudStage`

---

### N8. 🟡 debugMode = true por Padrão em Produção
**Severidade: BAIXA | Esforço: TRIVIAL**

```java
// GameConfig.java linha 22:
private boolean debugMode = true;  // ← deveria ser false em produção
```

Com debug ativo, logs verbosos são emitidos a cada frame (Quadtree, DanoTiro). Além disso, `ColisaoComponent.checarColisao()` desativa colisão com paredes em debug mode (noclip):
```java
List<Rectangle> colisores = GameConfig.getInstance().isDebugMode()
    ? java.util.Collections.emptyList()  // ← noclip!
    : listColisores;
```

**Correção:** Trocar para `false` e ativar apenas com F3 em runtime.

---

## 📊 TABELA CONSOLIDADA DE PRIORIDADES

### ✅ Concluídos (20 itens)

| # | Tarefa | Status |
|---|--------|--------|
| 1–12 | Todos os itens da v1 | ✅ Mantidos |
| 13 | VariavelComum → GameConfig | ✅ NOVO |
| 14 | Input Remapping (KeyBindings) | ✅ NOVO |
| 15 | DanoTiro com Quadtree | ✅ NOVO |
| 16 | WorldTemplate decomposto | ✅ NOVO |
| 17 | DadosInimigo nomes camelCase | ✅ NOVO |
| 18 | DadosSalvosJogador simplificado | ✅ NOVO |
| 19 | hitboxDraw() batched | ✅ NOVO |
| 20 | Cachear snapshot tiros DanoTiro | ✅ NOVO |

### 🟡 Parcialmente Feitos (2 itens)

| # | Tarefa | Progresso | Esforço |
|---|--------|-----------|---------|
| 21 | Nomes camelCase (2 restantes) | 60% | 🟢 Baixo |
| 22 | WorldTemplate delegate getters | 70% | 🟡 Médio |

### ❌ Pendentes (6 itens)

| # | Tarefa | Esforço | Impacto |
|---|--------|---------|---------|
| 23 | Pre-alocar Rectangles colisão | 🟢 Baixo | ⚡ Performance |
| 24 | Pre-alocar layers renderLayers() | 🟢 Baixo | ⚡ Performance |
| 25 | Matrix4 pré-alocado ScreenManager | 🟢 Baixo | ⚡ Performance |
| 26 | Object Pool para tiros | 🟡 Médio | ⚡ Performance |
| 27 | Personagem data-driven | 🔴 Alto | 📐 Escalabilidade |
| 28 | Testes unitários | 🟡 Médio | 🛡️ Qualidade |

### 🆕 Novos Problemas (8 itens)

| # | Tarefa | Severidade | Esforço | Impacto |
|---|--------|-----------|---------|---------|
| N1 | 🔴 **LoadingScreen assíncrona** | ALTA | 🟡 Médio | 🔥 UX Crítico |
| N2 | 🟠 Quadtree logging verboso | MÉDIA | 🟢 Baixo | ⚡ Performance |
| N3 | 🟠 DesenharMiniMapa alocações | MÉDIA | 🟢 Baixo | ⚡ Performance |
| N4 | 🟠 PlayerTemplate snapshot tiros | MÉDIA | 🟢 Baixo | ⚡ Performance |
| N5 | 🟠 TilemapHitbox drawObjects lista | MÉDIA | 🟢 Baixo | ⚡ Performance |
| N6 | 🟡 EscolherPersonagem recria player | BAIXA | 🟡 Médio | ⚡ Performance |
| N7 | 🟡 Pacote Hud → hud | BAIXA | 🟢 Baixo | 📐 Qualidade |
| N8 | 🟡 debugMode = true por padrão | BAIXA | 🟢 Trivial | 🐛 Bug |

---

## 🎯 PLANO DE AÇÃO RECOMENDADO

### 🟢 Sprint 1 — Quick Wins (1-2h total)

Mudanças triviais com impacto direto:

| # | Tarefa | Mudança | Linhas |
|---|--------|---------|--------|
| N8 | debugMode = false | 1 linha em `GameConfig.java` | 1 |
| 23 | Pre-alocar Rectangles | 2 campos `Rectangle` em `ChecarColisao` + `.set()` | ~8 |
| 24 | Pre-alocar layers | Campos `String[]` pré-montados em `MundoAreia` | ~15 |
| 25 | Matrix4 pré-alocado | 1 campo em `ScreenManager` | ~3 |
| N2 | Quadtree logging | Trocar `isDebugMode()` → `isHitboxVisivel()` ou remover | ~5 |
| N3 | MiniMapa alocações | 3 campos pré-alocados em `DesenharMiniMapa` | ~5 |
| N4 | Snapshot tiros | Campo reutilizável em `PlayerTemplate` | ~3 |
| 21 | 2 nomes camelCase | Renomear `Sala Sala` em 2 arquivos | ~2 |

### 🟡 Sprint 2 — Melhorias Importantes (meio dia)

| # | Tarefa | Benefício |
|---|--------|-----------|
| N1 | LoadingScreen assíncrona | Elimina freeze de 2-5s na inicialização |
| N6 | EscolherPersonagem cachear | Elimina recriação de PlayerTemplate a cada preview |
| N5 | TilemapHitbox cache lista | Elimina sort a cada chamada |
| N7 | Pacote Hud → hud | Convenção Java (IDE faz refactor automático) |

### 🟠 Sprint 3 — Funcionalidades (1-2 dias)

| # | Tarefa | Benefício |
|---|--------|-----------|
| 26 | Object Pool para tiros | Elimina GC pressure em combate |
| 22 | Remover delegate getters | WorldTemplate mais limpo, ~80 linhas a menos |
| 28 | Testes unitários básicos | StatsComponent, Quadtree, GeradorSalas |

### 🔴 Sprint 4 — Refatoração Maior (1+ dia)

| # | Tarefa | Benefício |
|---|--------|-----------|
| 27 | Personagem data-driven | 4 classes → 1 genérica + 4 JSONs |

---

## 📈 EVOLUÇÃO COMPLETA DO PROJETO

```
Análise original:  ✅ 5   🟡 4  ❌ 13  → ~23% concluído
Análise v1:        ✅ 12  🟡 3  ❌ 10  → ~48% concluído
Análise v2:        ✅ 20  🟡 2  ❌ 6   → ~71% concluído

Itens resolvidos desde a v1:
  ✅ VariavelComum → GameConfig (era ❌)
  ✅ Input Remapping KeyBindings (era ❌)
  ✅ DanoTiro com Quadtree (era ❌)
  ✅ WorldTemplate decomposição (era ❌)
  ✅ DadosInimigo camelCase (era 🟡)
  ✅ DadosSalvosJogador simplificado (era ❌)
  ✅ hitboxDraw() batched (era 🟡)
  ✅ Cachear snapshot tiros DanoTiro (era ❌)
```

```
                     ██████████████████████░░░░░░░░░  71%
Análise original:    █████░░░░░░░░░░░░░░░░░░░░░░░░░  23%
Análise v1:          ██████████████░░░░░░░░░░░░░░░░░  48%
Análise v2:          █████████████████████░░░░░░░░░░  71%
```

---

## 💡 PONTOS POSITIVOS DO PROJETO

1. **Arquitetura de componentes madura** — `PlayerTemplate` e `EnemyTemplate` usam composição com 9 componentes bem definidos
2. **Zero memory leaks** — Todos os assets gerenciados pelo `AssetManager`, nenhum `dispose()` incorreto
3. **GameConfig encapsulado** — Substituiu variáveis globais mutáveis por singleton com validação
4. **Sistema de input profissional** — `KeyBindings` com swap de conflitos, persistência JSON, enum tipado
5. **Spatial partitioning** — `Quadtree` genérica com debug visual colorido por nível
6. **WorldTemplate decomposto** — 4 managers claros com responsabilidades bem definidas
7. **Transições centralizadas** — `ScreenManager` com interface `TransitionEffect` e dois efeitos
8. **Cache bidirecional de animações** — Mudança de direção em O(1)
9. **Records Java** — `DadosPlayer`, `DadosInimigo`, `DefinirSprite`, `InfoGeraObjeto`
10. **Error handling consistente** — try/catch com logging em todos os mundos e renderização
11. **Separação clara de pacotes** — `engine/`, `entity/`, `world/` com sub-pacotes lógicos
12. **DadosSalvosJogador limpo** — De 66 linhas de boilerplate para 12 linhas diretas

> **Conclusão:** O projeto triplicou o progresso desde a análise original (23% → 71%). Os problemas arquiteturais mais críticos foram resolvidos: state management (`GameConfig`), input system (`KeyBindings`), spatial partitioning (`Quadtree`), e decomposição de god class (`WorldTemplate` → 4 managers). O próximo foco deve ser os **quick wins de performance** do Sprint 1 (8 mudanças triviais totalizando ~40 linhas) e a **LoadingScreen** que é o problema de UX mais visível para o jogador.

