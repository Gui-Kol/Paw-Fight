# Configuração do Workflow Auto-PR

## ⚠️ Importante: Personal Access Token (PAT)

O workflow `auto-pr.yml` foi criado para gerar Pull Requests automaticamente. Porém, devido a restrições de segurança do GitHub Actions, é necessário configurar um **Personal Access Token (PAT)** para que funcione corretamente.

### Por que é necessário?

GitHub Actions com `GITHUB_TOKEN` automático tem permissões limitadas quando acionado por eventos de `push`. Para criar ou modificar Pull Requests, é necessário um token com permissões explícitas.

### Como Configurar:

#### 1. Criar um Personal Access Token (PAT)

1. Acesse: https://github.com/settings/tokens
2. Clique em "Generate new token" → "Generate new token (classic)"
3. Configure as permissões:
   - ✅ `repo` (acesso completo ao repositório)
   - ✅ `workflow` (controle de workflows)
4. Copie o token gerado

#### 2. Adicionar o Token como Secret no Repositório

1. Vá para: https://github.com/Gui-Kol/Paw-Fight/settings/secrets/actions
2. Clique em "New repository secret"
3. Nome: `GITHUB_PAT`
4. Valor: Cole o token gerado
5. Clique em "Add secret"

#### 3. Pronto!

Agora o workflow `auto-pr.yml` funcionará corretamente e criará PRs automaticamente quando você fazer push em qualquer branch que não seja `main`.

### Alternativa: Usar o Workflow com GITHUB_TOKEN

Se você não quer criar um PAT, o workflow ainda tentará usar o `GITHUB_TOKEN` automático (que pode falhar), mas será acionado normalmente.

### Testando

Faça um push em uma nova branch para testar:

```bash
git checkout -b feature/teste
git commit -m "Teste do workflow auto-PR"
git push -u origin feature/teste
```

Verifique a aba "Actions" no GitHub para acompanhar a execução do workflow.

---

## Resumo das Mudanças

- ✅ Workflow `auto-pr.yml` criado
- ✅ Workflow `ci-cd.yml` limpo (removido job auto-pr antigo)
- ✅ Separação de responsabilidades: CI/CD e Auto-PR em workflows diferentes
- ✅ Configurado para usar PAT quando disponível


