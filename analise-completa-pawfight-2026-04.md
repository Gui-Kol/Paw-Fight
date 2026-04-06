# 🐾 Análise Completa — Paw Fight (Abril 2026)

## 📊 Visão Geral do Projeto

| Métrica | Valor |
|---------|-------|
| Arquivos `.java` | 69 |
| Linhas de código totais | 5.269 |
| Pacotes | 13 |
| Framework | libGDX |
| Build | Gradle |
| Testes | ❌ Zero |

### 🏗️ Arquitetura de Pacotes

```
com.pawfight.game
├── PawFight.java                          (128 linhas) — Game principal
├── engine/
│   ├── Assets.java                        (103) — AssetManager centralizado
│   ├── AudioEngine.java                   (28)  — Wrapper de áudio
│   ├── ScreenManager.java                 (106) — Gerenciador de transições ✨ NOVO
│   ├── Validar.java                       (9)   — Utilitário de validação
│   ├── VariavelComum.java                 (28)  — Estado global
│   ├── design/
│   │   ├── AlteradorZoom.java             (25)
│   │   ├── DefinirSprite.java             (10)  — record
│   │   ├── animation/MotorAnimacao.java   (123) — Motor de animação
│   │   ├── desenhar/DesenharTexto.java    (50)
│   │   ├── desenhar/DesenharTextura.java  (22)
│   │   └── transition/                    — FadeTransitionEffect, SlideTransitionEffect, TransicaoTela
│   ├── fisica/
│   │   ├── ChecarColisao.java             (33)
│   │   └── TilemapHitboxFactory.java      (136)
│   ├── font/FontFactory.java              (16)
│   ├── Hud/ ⚠️ (PascalCase)
│   │   ├── CriarBotao.java               (50)
│   │   ├── DesenharMiniMapa.java          (153)
│   │   ├── Hud.java                       (166)
│   │   ├── HudPause.java                  (119)
│   │   └── HudStage.java                  (58)
│   ├── procedural/
│   │   ├── CarregarPortas.java            (114)
│   │   ├── GerarInimigos.java             (85)
│   │   ├── GerarObjetos.java              (55)
│   │   ├── ObjetoGerado.java              (41)
│   │   └── sala/ (GeradorSalas, Sala, TipoSala, InfoGeraObjeto, Direction)
│   ├── render/
│   │   ├── Renderizar.java                (97)
│   │   └── RenderizadorCamada.java        (60)
│   └── save/
│       ├── DadosSalvosJogador.java        (66)
│       └── SalvarJogo.java                (20)
├── entity/
│   ├── Entidade.java                      (19)  — interface
│   ├── bosses/BossesTemplate.java         (3)
│   ├── component/                         — 9 componentes ✨ BEM FEITO
│   │   ├── AnimacaoComponent.java         (183) — com cache L/R
│   │   ├── AudioComponent.java            (39)
│   │   ├── CameraComponent.java           (43)
│   │   ├── ColisaoComponent.java          (46)
│   │   ├── CombateComponent.java          (57)
│   │   ├── InputComponent.java            (14)
│   │   ├── MovimentoComponent.java        (42)
│   │   ├── SaveComponent.java             (33)
│   │   └── StatsComponent.java            (154)
│   ├── enemy/
│   │   ├── DadosInimigo.java              (23)  — record (com nomes PascalCase)
│   │   ├── EnemyTemplate.java             (186) — refatorado com componentes ✨
│   │   ├── MoverDirecaoPlayer.java        (55)
│   │   └── Skeleton.java                  (80)
│   ├── player/
│   │   ├── DadosPlayer.java               (21)  — record ✨
│   │   ├── PlayerTemplate.java            (310) — usa componentes
│   │   ├── BlackBird.java                 (60)
│   │   ├── BlackCat.java                  (60)
│   │   ├── Dove.java                      (60)
│   │   └── OrangeCat.java                 (60)
│   └── tiro/
│       ├── Atirar.java                    (24)
│       ├── DanoTiro.java                  (24)
│       ├── TirosTemplate.java             (87)
│       └── dove/Coco.java                 (52)
└── world/
    ├── Home.java                          (136)
    ├── MundoAreia.java                    (207)
    ├── WorldTemplate.java                 (299)
    └── base/
        ├── Base.java                      (122)
        ├── EntradaPortais.java            (59)
        ├── EscolherPersonagem.java        (174)
        ├── ExibirDadosPersonagem.java     (77)
        └── Portoes.java                   (19)
```

---

## 📈 Resumo de Progresso vs Análise Anterior

| Status | Quantidade | Detalhes |
|--------|-----------|----------|
| ✅ Feito | **12** | +7 desde a última análise |
| 🟡 Parcialmente feito | **3** | -1 desde a última análise |
| ❌ Não feito | **10** | -3 desde a última análise |
| 🆕 Novos problemas encontrados | **6** | Diferentes dos anteriores |

**Progresso geral: ~48% completo** (vs ~23% na análise anterior)

---

## ✅ O QUE JÁ FOI FEITO (12 itens)

---

### 1. ✅ AssetManager Centralizado
**Item original: #1 | Status: CONCLUÍDO**

`Assets.java` (103 linhas) funciona como gerenciador centralizado:
- `loadAll()` carrega texturas e músicas
- `get(path, type)` delega ao `AssetManager`
- `dispose()` libera todos os assets de uma vez
- Zero `new Texture()` no projeto (exceto Pixmaps dinâmicos em `FadeTransitionEffect`/`SlideTransitionEffect`)
- Todos os `dispose()` têm comentário: *"Texturas são gerenciadas pelo AssetManager — NÃO dar dispose aqui"*

> 🎯 Execução exemplar.

---

### 2. ✅ EnemyTemplate Refatorado com Componentes
**Item original: #2 | Status: CONCLUÍDO**

`EnemyTemplate.java` reduziu de ~348 para **186 linhas** (–46%) e agora usa:
- `AnimacaoComponent animacao` — com cache esquerda/direita
- `AudioComponent audio` — tocar dano/morte delegado
- `StatsComponent stats` — `aplicarDano()`, `updateTimers()` delegados
- Método `drawSprite(SpriteBatch)` sem `begin()/end()` próprio — compatível com batching externo

> 🎯 Refatoração limpa. Segue o mesmo padrão do `PlayerTemplate`.

---

### 3. ✅ Texturas Compartilhadas nos Inimigos
**Item original: #3 | Status: CONCLUÍDO**

`Skeleton.java` usa `Assets.get(...)` para todas as texturas:
```java
Assets.get("entitys/enemy/Skeleton/Idle.png", Texture.class)
```
`cloneEnemy()` chama `new Skeleton(...)` que reutiliza as mesmas texturas do cache.

---

### 4. ✅ Batch begin/end de Sprites Centralizado
**Item original: #5 | Status: CONCLUÍDO**

- `Renderizar.renderizarInimigos()` — **1 único** `batch.begin()/end()` para todos os sprites de inimigos
- `Renderizar.renderizarObjects()` — **1 único** `batch.begin()/end()` para todos os objetos
- `PlayerTemplate.desenharTiros()` — **1 único** `batch.begin()/end()` para todos os tiros
- `EnemyTemplate.drawSprite()` — não faz `begin()/end()` (só `batch.draw()`)

---

### 5. ✅ Dispose Correto com AssetManager
**Status: CONCLUÍDO**

Nenhuma classe faz `dispose()` em texturas/músicas gerenciadas pelo `AssetManager`. O `Assets.dispose()` é chamado uma vez em `PawFight.dispose()`.

---

### 6. ✅ ScreenManager Criado ✨ NOVO DESDE ÚLTIMA ANÁLISE
**Item original: #6 | Status: CONCLUÍDO**

O `ScreenManager.java` (106 linhas) foi implementado:
- Singleton inicializado em `PawFight.create()` via `ScreenManager.init(this)`
- `fadeToScreen(Screen, duration, color, gradient)` — transição com fade
- `slideToScreen(Screen, duration, direction)` — transição com slide
- `update(delta)` gerencia o ciclo de vida da transição (fade-out → troca tela → fade-in)
- `render(batch)` desenha o efeito sobre o conteúdo atual
- Suporte a `FadeTransitionEffect` e `SlideTransitionEffect`
- Dispose automático do efeito anterior ao iniciar nova transição

**Uso correto em:**
- `PawFight.java`: `ScreenManager.getInstance().fadeToScreen(new Home(...), 1f, Color.BLACK, false)`
- `Home.java`: `ScreenManager.getInstance().fadeToScreen(new Base(...), 1.5f, Color.BLACK, false)`

> 🎯 Eliminou as transições manuais com `Timer.schedule` e `TransicaoTela` em cada tela.

---

### 7. ✅ Cache de Animações Esquerda/Direita ✨ NOVO DESDE ÚLTIMA ANÁLISE
**Item original: #4 | Status: CONCLUÍDO**

`AnimacaoComponent.java` (183 linhas) agora tem sistema de cache bidirecional:

```java
// Cache: [0] = direita, [1] = esquerda
private final Animation<TextureRegion>[] idleCache = new Animation[2];
private final Animation<TextureRegion>[] walkCache = new Animation[2];
// ... etc para cada tipo de animação
```

- `rebuildAnimations()` constrói **ambas** as direções de uma vez
- `checkDirectionChange(boolean)` verifica se a direção mudou:
  - Se cache existe → chama `applyDirection()` (apenas troca ponteiros, **SEM rebuild**)
  - Se cache não existe → marca `animationsDirty = true`
- `animaAtual()` faz rebuild lazy se `animationsDirty == true`

> 🎯 Excelente implementação! Mudanças de direção agora são O(1) — apenas troca de referência.

---

### 8. ✅ Hitbox Draws Batched ✨ NOVO DESDE ÚLTIMA ANÁLISE
**Item original: #5 parcial | Status: CONCLUÍDO (maioria)**

Todos os métodos de batch em `Renderizar.java` agora fazem um único `begin()/end()`:
- `hitBoxDrawList()` (linhas 92-100) — loop dentro de um único begin/end
- `hitBoxListObjeto()` (linhas 102-111) — loop dentro de um único begin/end
- `renderizarInimigos()` hitboxes (linhas 43-50) — loop dentro de um único begin/end
- `TilemapHitboxFactory.draw()` (linhas 154-165) — loop dentro de um único begin/end
- `PlayerTemplate.desenharTiros()` hitboxes (linhas 221-228) — batched

⚠️ **Exceção:** `hitboxDraw()` (linhas 85-90) ainda faz begin/end individual — usado para hitbox do player.

---

### 9. ✅ Animação do Coração Cacheada no HUD ✨ NOVO DESDE ÚLTIMA ANÁLISE
**Item original: #15 | Status: CONCLUÍDO**

```java
// No construtor do Hud — criada UMA vez:
coracaoAnimation = MotorAnimacao.animar(coracaoDefinition);

// Em desenharCoracao() — apenas consulta o cache:
coracaoAnimation.getKeyFrames()[frameIndex]
```

Não recria mais animação a cada frame. A animação do coração é construída uma vez no construtor.

---

### 10. ✅ SalvarJogo.salvar() Renomeado ✨ NOVO DESDE ÚLTIMA ANÁLISE
**Item original: #11 | Status: CONCLUÍDO**

O método `SalvarJogo()` foi renomeado para `salvar()`:
```java
// SalvarJogo.java
public void salvar(DadosSalvosJogador data) { ... }

// PawFight.java
SalvarJogo.salvar(player.saveData());
```

---

### 11. ✅ removeIf() em Inimigos Mortos ✨ NOVO DESDE ÚLTIMA ANÁLISE
**Item N2 da análise anterior | Status: CONCLUÍDO**

```java
// Renderizar.java — sem mais ArrayList temporário
public void atualizarListaInimigos(float delta, List<EnemyTemplate> listaInimigos) {
    for (EnemyTemplate enemy : listaInimigos) {
        enemy.update(delta);
    }
    listaInimigos.removeIf(EnemyTemplate::isMorto);
}
```

---

### 12. ✅ EnemyTemplate.draw() Individual Removido ✨ NOVO DESDE ÚLTIMA ANÁLISE
**Item N3 da análise anterior | Status: CONCLUÍDO**

O antigo `EnemyTemplate.draw()` com `batch.begin()/end()` individual foi removido. Agora existe apenas `drawSprite(SpriteBatch batch)` que faz apenas `batch.draw()` sem gerenciar o begin/end.

---

## 🟡 PARCIALMENTE FEITO (3 itens)

---

### 13. 🟡 Nomes de Variáveis com Nome de Classe
**Item original: #10 | Progresso: ~30%**

**O que melhorou:**
- `Home.java` não tem mais `TransicaoTela` (usa `ScreenManager`)
- Componentes estão bem nomeados: `animacao`, `audio`, `stats`, `combate`, `colisao`

**O que ainda viola convenções (variável = nome da classe):**

| Arquivo | Linha | Violação |
|---------|-------|----------|
| `WorldTemplate.java` | 47 | `protected RenderizadorCamada RenderizadorCamada;` |
| `WorldTemplate.java` | 62 | `protected GeradorSalas GeradorSalas;` |
| `MundoAreia.java` | 31 | `GeradorSalas = new GeradorSalas();` |
| `MundoAreia.java` | 53 | `public void logRoomInfo(Sala Sala)` |
| `Base.java` | 104 | `public void logRoomInfo(Sala Sala)` |
| `Hud.java` | 26 | `private final MotorAnimacao MotorAnimacao;` |
| `HudPause.java` | 23 | `private final MotorAnimacao MotorAnimacao;` |
| `PawFight.java` | 136 | `SalvarJogo SalvarJogo = new SalvarJogo();` |
| `PawFight.java` | 141 | `SalvarJogo SalvarJogo = new SalvarJogo();` |

**Correções sugeridas:**
- `RenderizadorCamada` → `renderizadorCamada`
- `GeradorSalas` → `geradorSalas`
- `MotorAnimacao` → `motorAnimacao`
- `SalvarJogo SalvarJogo` → `SalvarJogo salvarJogo`
- `Sala Sala` → `Sala sala`

---

### 14. 🟡 DadosInimigo com Nomes PascalCase
**Item original: #9 | Progresso: ~0%**

`DadosInimigo.java` é um `record`, mas usa PascalCase nos campos:

```java
public record DadosInimigo(
    String nome,           // ✅ OK
    int VidaBase,          // ❌ deveria ser vidaBase
    int Forca,             // ❌ deveria ser forca
    int Velocidade,        // ❌ deveria ser velocidade
    int Tamanho,           // ❌ deveria ser tamanho
    int HitboxSize,        // ❌ deveria ser hitboxSize
    int HitboxOffsetY,     // ❌ deveria ser hitboxOffsetY
    int HitboxOffsetX,     // ❌ deveria ser hitboxOffsetX
    ...
```

**Impacto:** Todos os locais que chamam `dadosInimigo.VidaBase()`, `dadosInimigo.Forca()`, etc., precisam ser atualizados. Afeta `EnemyTemplate.java` (linhas 62-64) e `Skeleton.java` (linhas 49-54).

---

### 15. 🟡 hitboxDraw() Individual para Player
**Item original: #5 | Progresso: ~85%**

O único `begin()/end()` individual restante é `Renderizar.hitboxDraw()`:
```java
public void hitboxDraw(ShapeRenderer shapeRenderer, Rectangle hitbox) {
    if (!HITBOX_ISVISIBLE) return;
    shapeRenderer.begin(ShapeRenderer.ShapeType.Line); // ← individual
    hitboxRect(shapeRenderer, hitbox, Color.RED);
    shapeRenderer.end();                                // ← individual
}
```

Chamado em:
- `PlayerTemplate.draw()` (linha 201) — para hitbox do player
- `TirosTemplate.desenharHitbox()` (linha 55) — mas tiros já têm batching no `PlayerTemplate`

**Correção sugerida:** Mover o begin/end para fora, ou integrar ao fluxo já batched.

---

## ❌ O QUE NÃO FOI FEITO (10 itens)

---

### 16. ❌ WorldTemplate Continua God Class
**Item original: #7 | Esforço: ALTO**

`WorldTemplate.java` tem **299 linhas** e **25+ campos**:

```
Campos: stage, tilemapHitboxFactory, RenderizadorCamada, map, player,
        desenharMiniMapa, carregarPortas, gerarObjetos, renderizar,
        errorFinal, listaObjetos, listaObjetosHitbox, rooms, GeradorSalas,
        currentRoom, podeEntrarPorta, salasVisitadas, danoTiro, shapeRenderer,
        background, game, batch, backMusic, camera, viewport, gerarInimigos,
        listaInimigos
```

**Subsistemas sugeridos para extração:**
- `WorldRenderer` — `renderLayers()`, `renderLayersUp()`, `renderizarInimigos()`, `renderizarObjects()`
- `WorldPhysics` — `carregarParede()`, `danoTiro`, colisão
- `RoomManager` — `GeradorSalas`, `rooms`, `currentRoom`, `salasVisitadas`, `checkPortals()`
- `EnemyManager` — `listaInimigos`, `gerarInimigos`, `atualizarListaInimigos()`

---

### 17. ❌ DadosSalvosJogador — DTO com Boilerplate
**Item original: #8 | Esforço: BAIXO**

`DadosSalvosJogador.java` tem **66 linhas** de getters/setters para 8 campos.

⚠️ **Nota:** Não pode ser convertido diretamente para `record` porque a desserialização JSON do libGDX (`com.badlogic.gdx.utils.Json`) precisa de construtor vazio + setters. Alternativa: usar Lombok `@Data` ou manter como está mas com campos `public`.

---

### 18. ❌ Input Hardcoded
**Item original: #12 | Esforço: MÉDIO**

`InputComponent.java` (14 linhas) tem todas as teclas hardcoded:
```java
public boolean isMoveRight()  { return Gdx.input.isKeyPressed(Input.Keys.D); }
public boolean isMoveLeft()   { return Gdx.input.isKeyPressed(Input.Keys.A); }
public boolean isMoveUp()     { return Gdx.input.isKeyPressed(Input.Keys.W); }
public boolean isMoveDown()   { return Gdx.input.isKeyPressed(Input.Keys.S); }
public boolean isAttackSpecial() { return Gdx.input.isKeyJustPressed(Input.Keys.SPACE); }
public boolean isAbility()       { return Gdx.input.isKeyJustPressed(Input.Keys.R); }
```

Sem sistema de remapeamento. O jogador não pode personalizar controles.

---

### 19. ❌ VariavelComum — Estado Global Mutável
**Item original: #13 | Esforço: MÉDIO**

`VariavelComum.java` ainda é uma coleção de variáveis estáticas mutáveis:
```java
public static boolean DEBUG_MODE = false;
public static boolean HITBOX_ISVISIBLE = false;
public static float VOLUME_MUSICA = 0.3f;
public static float VOLUME_EFEITOS = 0.7f;
public static float VOLUME_PASSOS = 0.2f;
```

Sem encapsulamento, sem validação, qualquer classe pode mudar qualquer valor a qualquer momento. Deveria ser um `GameConfig` injetado onde necessário.

---

### 20. ❌ DanoTiro O(n×m) sem Spatial Partitioning
**Item original: #14 | Esforço: MÉDIO**

`DanoTiro.darDanoListaInimigos()` itera **todos os tiros × todos os inimigos**:
```java
for (EnemyTemplate inimigo : inimigos) {
    for (TirosTemplate tiro : tirosSnapshot) {
        if (inimigo.getHitBox().overlaps(tiro.getHitBox())) {
            inimigo.dano(tiro.getDano());
        }
    }
}
```

Com poucos inimigos/tiros isso funciona, mas escala quadraticamente. Para muitos inimigos seria necessário grid ou quadtree.

---

### 21. ❌ renderLayers() Aloca ArrayList a Cada Frame
**Item original: #16 | Esforço: BAIXO**

`MundoAreia.renderLayers()` (linha 155) e `renderLayersUp()` (linha 186):
```java
List<String> layers = new ArrayList<>();  // ← TODA frame (~60x/s)
// ... popula a lista ...
layers.toArray(new String[0])             // ← TODA frame (~60x/s)
```

**Correção sugerida:** Pre-alocar as listas como campos, ou usar `String[]` direto com flag de tamanho.

---

### 22. ❌ ChecarColisao Cria 2 Rectangles por Frame
**Item original: #17 | Esforço: BAIXO**

```java
// ChecarColisao.java linhas 20 e 25
Rectangle nextHitboxX = new Rectangle(nextX, playerHitbox.y, playerHitbox.width, playerHitbox.height);
Rectangle nextHitboxY = new Rectangle(playerHitbox.x, nextY, playerHitbox.width, playerHitbox.height);
```

**Correção:** Usar 2 `Rectangle` como campos reutilizáveis com `.set()`.

---

### 23. ❌ Object Pool para Projéteis
**Item original: #19 | Esforço: MÉDIO**

`Atirar.atira()` cria novos objetos com `tiroModelo.clonar(player)` a cada tiro. `CombateComponent.updateTiros()` chama `tiro.dispose()` quando expirado. Não existe `Pool<TirosTemplate>`.

O libGDX tem `com.badlogic.gdx.utils.Pool` pronto para uso.

---

### 24. ❌ Personagem Data-Driven
**Item original: #21 | Esforço: ALTO**

4 classes quase idênticas (60 linhas cada):
- `BlackBird.java`, `BlackCat.java`, `Dove.java`, `OrangeCat.java`

Todas repetem o mesmo padrão:
```java
dadosPlayer() → return new DadosPlayer(stats...)
updateSpriteDefinitions() → animacao.setDefinitions(idle, walk, dead, hurt)
ataqueBasico() → vazio
ataqueEspecial() → vazio
usarHabilidadeEspecial() → vazio
```

Poderia ser um único `PlayerFromConfig` carregando dados de JSON/config.

---

### 25. ❌ Testes Automatizados
**Item original: #22 | Esforço: MÉDIO**

- Zero testes no projeto
- `core/build.gradle` não tem JUnit
- Sem diretório `src/test`
- Classes testáveis sem dependência do libGDX: `StatsComponent`, `ChecarColisao`, `SalvarJogo`, `DadosInimigo`, `GeradorSalas`

---

## 🆕 NOVOS PROBLEMAS ENCONTRADOS (6 itens)

---

### N1. 🔴 Assets.loadAll() Bloqueia a Thread Principal
**Severidade: ALTA | Esforço: MÉDIO**

```java
public static void loadAll() {
    loadTextures();
    loadMusic();
    manager.finishLoading();  // ← BLOQUEIA até tudo carregar
}
```

O jogo congela durante o carregamento. Não existe `LoadingScreen`.

**Recomendação:** Criar `LoadingScreen` que chama `manager.update()` em loop e mostra barra de progresso com `manager.getProgress()`.

---

### N2. 🟠 ScreenManager.render() Aloca Matrix4 a Cada Frame
**Severidade: MÉDIA | Esforço: BAIXO**

```java
// ScreenManager.java linha 105
Matrix4 oldMatrix = new Matrix4(batch.getProjectionMatrix()); // ← TODA frame durante transição
```

**Correção:** Usar campo `private final Matrix4 tempMatrix = new Matrix4()` e copiar com `tempMatrix.set(...)`.

---

### N3. 🟠 DanoTiro Cria ArrayList Snapshot por Frame
**Severidade: MÉDIA | Esforço: BAIXO**

```java
// DanoTiro.java linha 21
List<TirosTemplate> tirosSnapshot = new ArrayList<>(tiros); // ← TODA frame
```

E também em `PlayerTemplate.desenharTiros()` (linha 207):
```java
List<TirosTemplate> tirosSnapshot = new ArrayList<>(combate.getTiros());
```

**Correção:** Usar lista pré-alocada como campo, ou iterar com índice (for-i) para evitar `ConcurrentModificationException`.

---

### N4. 🟠 MundoAreia.getInfoObjetos() Cria ArrayList Toda Chamada
**Severidade: BAIXA | Esforço: BAIXO**

```java
public List<InfoGeraObjeto> getInfoObjetos() {
    List<InfoGeraObjeto> infoGeraObjetoList = new ArrayList<>(); // toda chamada
    infoGeraObjetoList.add(new InfoGeraObjeto(...));
    return infoGeraObjetoList;
}
```

Apesar de melhorado (cacheia `cactoTexture`), ainda recria a lista. Deveria ser `List.of(...)` ou campo cacheado.

---

### N5. 🟡 Pacote `Hud` com H Maiúsculo
**Severidade: BAIXA | Esforço: BAIXO**

O pacote é `com.pawfight.game.engine.Hud` (PascalCase) em vez de `com.pawfight.game.engine.hud`. Viola convenção Java onde pacotes devem ser lowercase.

**Impacto:** 5 arquivos nesse pacote + todos os imports que referenciam `engine.Hud.*`.

---

### N6. 🟡 TilemapHitboxFactory.drawObjects() Cria ArrayList por Chamada
**Severidade: BAIXA | Esforço: BAIXO**

```java
// TilemapHitboxFactory.java linha 83-86
List<MapObject> objects = new ArrayList<>();
for (MapObject obj : layer.getObjects()) {
    objects.add(obj);
}
objects.sort(...);
```

Cria lista + ordena a cada chamada. Deveria ser cacheado ou pré-ordenado.

---

## 📊 TABELA CONSOLIDADA DE PRIORIDADES

| # | Tarefa | Status | Esforço | Impacto |
|---|--------|--------|---------|---------|
| 1 | ~~AssetManager~~ | ✅ FEITO | — | — |
| 2 | ~~EnemyTemplate com componentes~~ | ✅ FEITO | — | — |
| 3 | ~~Texturas compartilhadas inimigos~~ | ✅ FEITO | — | — |
| 4 | ~~Batch begin/end sprites~~ | ✅ FEITO | — | — |
| 5 | ~~Dispose correto~~ | ✅ FEITO | — | — |
| 6 | ~~ScreenManager~~ | ✅ FEITO | — | — |
| 7 | ~~Cache animações L/R~~ | ✅ FEITO | — | — |
| 8 | ~~Hitbox draws batched~~ | ✅ FEITO | — | — |
| 9 | ~~Animação coração HUD~~ | ✅ FEITO | — | — |
| 10 | ~~SalvarJogo.salvar()~~ | ✅ FEITO | — | — |
| 11 | ~~removeIf() inimigos~~ | ✅ FEITO | — | — |
| 12 | ~~draw() individual enemy~~ | ✅ FEITO | — | — |
| **13** | Pre-alocar Rectangles colisão | ❌ Pendente | 🟢 Baixo | ⚡ Performance |
| **14** | Pre-alocar layers renderLayers() | ❌ Pendente | 🟢 Baixo | ⚡ Performance |
| **15** | Matrix4 pré-alocado ScreenManager | ❌ Pendente | 🟢 Baixo | ⚡ Performance |
| **16** | Cachear snapshot tiros DanoTiro | ❌ Pendente | 🟢 Baixo | ⚡ Performance |
| **17** | Corrigir nomes camelCase | 🟡 Parcial | 🟢 Baixo | 📐 Qualidade |
| **18** | DadosInimigo nomes PascalCase→camel | 🟡 Parcial | 🟢 Baixo | 📐 Qualidade |
| **19** | hitboxDraw() individual → batched | 🟡 Parcial | 🟢 Baixo | ⚡ Performance |
| **20** | Renomear pacote Hud → hud | ❌ Pendente | 🟢 Baixo | 📐 Qualidade |
| **21** | getInfoObjetos() cachear | ❌ Pendente | 🟢 Baixo | ⚡ Performance |
| **22** | DadosSalvosJogador simplificar | ❌ Pendente | 🟢 Baixo | 📐 Qualidade |
| **N1** | 🔴 **LoadingScreen assíncrona** | ❌ Pendente | 🟡 Médio | 🔥 UX Crítico |
| **23** | Input remapping | ❌ Pendente | 🟡 Médio | 🎮 Gameplay |
| **24** | VariavelComum → GameConfig | ❌ Pendente | 🟡 Médio | 📐 Arquitetura |
| **25** | Object Pool para tiros | ❌ Pendente | 🟡 Médio | ⚡ Performance |
| **26** | Testes unitários | ❌ Pendente | 🟡 Médio | 🛡️ Qualidade |
| **27** | WorldTemplate extrair subsistemas | ❌ Pendente | 🔴 Alto | 📐 Arquitetura |
| **28** | Personagem data-driven | ❌ Pendente | 🔴 Alto | 📐 Escalabilidade |

---

## 🎯 PLANO DE AÇÃO RECOMENDADO

### 🟢 Sprint 1 — Quick Wins de Performance (1-2h total)

Todas são mudanças de poucas linhas com impacto direto em performance:

| # | Tarefa | Mudança |
|---|--------|---------|
| 13 | Pre-alocar Rectangles | 2 campos `Rectangle` em `ChecarColisao` + usar `.set()` |
| 14 | Pre-alocar layers | Campos `String[]` pré-montados em `MundoAreia` |
| 15 | Matrix4 pré-alocado | 1 campo `Matrix4` em `ScreenManager` |
| 16 | Cachear snapshot tiros | Campo `List` reutilizável em `DanoTiro` e `PlayerTemplate` |
| 21 | getInfoObjetos() cachear | `List.of(...)` em vez de `new ArrayList<>()` |
| 19 | hitboxDraw() batched | Remover begin/end, integrar ao fluxo existente |

### 🟡 Sprint 2 — Naming & Convenções (1-2h total)

| # | Tarefa | Mudança |
|---|--------|---------|
| 17 | Nomes camelCase | Renomear 9 variáveis em 5 arquivos |
| 18 | DadosInimigo camelCase | Renomear 7 campos do record + 2 arquivos de uso |
| 20 | Pacote Hud → hud | Mover 5 arquivos + atualizar imports (IDE faz automaticamente) |

### 🟠 Sprint 3 — Funcionalidades Importantes (meio dia cada)

| # | Tarefa | Benefício |
|---|--------|-----------|
| N1 | LoadingScreen assíncrona | Elimina freeze de ~2-3s no inicio do jogo |
| 25 | Object Pool para tiros | Elimina GC pressure em cenas de combate |
| 23 | Input remapping | Jogador pode customizar controles |

### 🔴 Sprint 4 — Refatorações Arquiteturais (1+ dia cada)

| # | Tarefa | Benefício |
|---|--------|-----------|
| 27 | WorldTemplate subsistemas | God class → classes focadas e testáveis |
| 28 | Personagem data-driven | 4 classes quase idênticas → 1 genérica + configs JSON |
| 26 | Testes unitários | Prevenir regressões, facilitar refatoração |

---

## 📈 EVOLUÇÃO DESDE A ÚLTIMA ANÁLISE

```
Última análise:  ✅ 5  🟡 4  ❌ 13  → ~23% concluído
Análise atual:   ✅ 12 🟡 3  ❌ 10  → ~48% concluído

Itens resolvidos desde a última vez:
  ✅ ScreenManager (era ❌)
  ✅ Cache animações L/R (era ❌)
  ✅ Animação coração HUD (era ❌)
  ✅ SalvarJogo.salvar() renomeado (era ❌)
  ✅ removeIf() em inimigos mortos (era 🆕)
  ✅ draw() individual de enemy removido (era 🆕)
  ✅ Hitbox draws batched (era 🟡, agora ✅)
```

---

## 💡 PONTOS POSITIVOS DO PROJETO

1. **Arquitetura de componentes** — `PlayerTemplate` e `EnemyTemplate` usam composição em vez de herança profunda
2. **AssetManager** — Zero memory leaks de texturas
3. **ScreenManager** — Transições centralizadas e limpas
4. **AnimacaoComponent** — Cache bidirecional sofisticado
5. **Records** — `DadosPlayer`, `DadosInimigo`, `DefinirSprite`, `InfoGeraObjeto` usam records Java
6. **Error handling** — try/catch com logging consistente em mundos e renderização
7. **Singleton Renderizar** — Centraliza lógica de desenho sem duplicar
8. **Separação clara** — `engine/`, `entity/`, `world/` bem organizados

> **Conclusão:** O projeto dobrou o progresso desde a última análise. Os problemas mais críticos (memory leaks, duplicação de código, animação rebuild) foram resolvidos. O próximo foco deve ser nos **quick wins de performance** (Sprint 1) que são 6 mudanças triviais, seguidos da **LoadingScreen** que é o problema de UX mais visível para o jogador.

