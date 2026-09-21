# PawFight 🐾

![Status](https://img.shields.io/badge/status-em%20desenvolvimento-yellow)![Linguagem](https://img.shields.io/badge/linguagem-Java%2017-orange)![Framework](https://img.shields.io/badge/framework-LibGDX%201.14.0-red)

_Um frenético jogo Roguelike de sobrevivência onde animais heroicos enfrentam hordas de monstros em masmorras geradas proceduralmente._

## 📜 Sobre o Jogo

**PawFight** é um jogo de ação 2D com elementos de Roguelike, desenvolvido em Java com a poderosa biblioteca LibGDX. Inspirado em clássicos modernos como **Soul Knight** e **Vampire Survivors**, o jogo te coloca na pele de bravos animais guerreiros que devem lutar por sua sobrevivência contra ondas intermináveis de monstros em fases que mudam a cada partida.

Prepare-se para uma experiência cheia de adrenalina, onde cada decisão importa e cada power-up pode ser a chave para a vitória (ou a derrota!).

## ✨ Funcionalidades Principais

- **🐾 Personagens Carismáticos:** Jogue com uma variedade de animais — cada um com atributos, projéteis e habilidades exclusivos:
  - **Orange Cat** 🐱 — resistente, dispara o *Tiro de Sangue*.
  - **Black Cat** 🐈‍⬛ — dispara o misterioso *Tiro Fantasmagórico*.
  - **Dove** 🕊️ — rápida e ágil, arremessa o *Tiro de Coco* e o *Tiro Solar*.
  - **Black Bird** 🐦‍⬛ — congela os inimigos com o *Tiro de Gelo*.
- **🗺️ Fases Procedurais:** Nunca jogue o mesmo mapa duas vezes! As salas da masmorra são geradas aleatoriamente, com portas, objetos e encontros distribuídos de forma única a cada partida.
- **⚔️ Sobrevivência contra Hordas:** Enfrente exércitos de esqueletos que perseguem o jogador e testam seus reflexos e sua estratégia.
- **👑 Chefões Épicos:** Ao final da jornada, enfrente o poderoso **Rei Esqueleto** em uma arena de boss dedicada.
- **🗺️ Minimapa e HUD completos:** Acompanhe vida, status e a navegação pelas salas em tempo real.
- **💾 Sistema de Save:** Seu progresso fica salvo para continuar a aventura depois.
- **🔊 Trilha e efeitos sonoros:** Engine de áudio com controle de volumes independente (música, efeitos e passos).

## 🕹️ Gameplay

O objetivo é simples: **sobreviver**. Escolha seu personagem na base, entre no portal, explore as salas geradas proceduralmente, derrote hordas de inimigos e prepare-se para o confronto final contra o chefe da fase.

### ⌨️ Controles

| Tecla | Ação |
|-------|------|
| `W` `A` `S` `D` | Movimentar o personagem |
| `Espaço` | Ataque especial |
| `R` | Habilidade |
| `Esc` | Pausar |
| `←` `→` `Enter` | Navegar e confirmar nos menus |
| `+` / `-` | Zoom da câmera |
| `F11` | Alternar tela cheia |
| `F3` | Modo debug |
| `F6` | Cheat |

## 💡 Inspirações

Este projeto busca unir o melhor de dois mundos:
- **Soul Knight:** Pela ação rápida, o estilo de exploração de "salas" e personagens únicos com armas exclusivas.
- **Vampire Survivors:** Pela intensidade de lutar contra hordas massivas e a satisfação de se tornar uma máquina de destruição.

## 🛠️ Tecnologias Utilizadas

- **Linguagem:** [Java 17](https://www.java.com/)
- **Framework Gráfico:** [LibGDX](https://libgdx.com/) 1.14.0 (backend LWJGL3)
- **Gerenciador de Build:** [Gradle](https://gradle.org/)
- **Testes:** JUnit 5 + Mockito

## 📂 Estrutura do Projeto

```
Paw-Fight/
├── core/                  # Lógica principal do jogo (multiplataforma)
│   └── src/
│       ├── main/java/com/pawfight/game/
│       │   ├── engine/    # Núcleo: física (quadtree), procedural, HUD,
│       │   │              # áudio, save, input, renderização e partículas
│       │   ├── entity/    # Entidades: players, inimigos, bosses e projéteis
│       │   └── world/     # Telas/mundos: Home (base) e MundoAreia
│       └── test/          # Testes unitários (JUnit 5 + Mockito)
├── lwjgl3/                # Launcher desktop (LWJGL3) e empacotamento
├── assets/                # Sprites, áudios, fontes e mapas
└── art/                   # Fontes de arte brutas do projeto
```

## 🚧 Status do Projeto

⚠️ **PawFight** está atualmente em **fase de desenvolvimento ativo**. Muitas funcionalidades ainda estão sendo implementadas e o jogo pode conter bugs ou estar incompleto. O feedback e as contribuições da comunidade são muito bem-vindos!

## 🚀 Como Começar

**Pré-requisito:** JDK 17 ou superior instalado.

1. **Clone o repositório:**
   ```sh
   git clone https://github.com/Gui-Kol/Paw-Fight.git
   ```
2. **Navegue até o diretório do projeto:**
   ```sh
   cd Paw-Fight
   ```
3. **Execute o jogo (via Gradle):**
   ```sh
   # Windows
   .\gradlew lwjgl3:run

   # Linux/macOS
   ./gradlew lwjgl3:run
   ```
   > Se preferir, você pode importar o projeto em sua IDE favorita (IntelliJ IDEA, Eclipse, etc.) como um projeto Gradle.

### 📦 Gerando um executável (.jar)

```sh
# JAR multiplataforma (build/libs/PawFight-<versao>.jar)
./gradlew lwjgl3:jar

# JARs otimizados por plataforma
./gradlew lwjgl3:jarWin lwjgl3:jarLinux lwjgl3:jarMac
```

## 🧪 Rodando os Testes

O projeto possui testes unitários para os sistemas centrais (física/colisão, geração procedural, combate, status, etc.):

```sh
./gradlew core:test
```

## 🤝 Como Contribuir

Contribuições são o que tornam a comunidade de código aberto um lugar incrível para aprender, inspirar e criar. Qualquer contribuição que você fizer será **muito apreciada**.

1. Faça um Fork do projeto
2. Crie sua Feature Branch (`git checkout -b feature/NovaFuncionalidade`)
3. Faça o Commit de suas mudanças (`git commit -m 'Adiciona NovaFuncionalidade'`)
4. Faça o Push para a Branch (`git push origin feature/NovaFuncionalidade`)
5. Abra um Pull Request

## 📄 Licença

Distribuído sob a Licença MIT. Veja `LICENSE` para mais informações.

---

_Feito com ❤️ e muita cafeína._
