# Configuração do Workflow Auto-PR

## Importante: Personal Access Token (PAT)

O workflow `ci-cd.yml` agora inclui um job de Auto-PR que cria Pull Requests automaticamente. Este job requer um **Personal Access Token (PAT)**.

### Por que é necessário?

GitHub Actions com `GITHUB_TOKEN` automático tem permissões limitadas. Para criar PRs, é necessário um token específico.

### Como Configurar:

#### 1. Criar um Personal Access Token (PAT)

1. Acesse: https://github.com/settings/tokens
2. Clique em "Generate new token (classic)"
3. Configure as permissões **OBRIGATÓRIAS**:
   - ✅ `repo` (Full control of private repositories)
   - ✅ `workflow` (Update GitHub Action workflows)
4. Gere e copie o token imediatamente

#### 2. Adicionar o Token como Secret no Repositório

1. Vá para: https://github.com/Gui-Kol/Paw-Fight/settings/secrets/actions
2. Clique em "New repository secret"
3. Nome: `PAT` (sem o prefixo GITHUB_)
4. Valor: Cole o token gerado
5. Save

## Como o Workflow Funciona

O `ci-cd.yml` agora contém os seguintes jobs:

1. **lint-format**: Compila o código Java
2. **qodana**: Análise de código com Qodana
3. **sonarcloud**: Análise com SonarCloud
4. **security-scan**: Scan de segurança com OWASP
5. **create-pr**: Cria PR automaticamente (este job é independente dos outros)
6. **release-drafter**: Cria draft de release (apenas em main)
7. **build-multiplatform**: Build para múltiplas plataformas (apenas em tags)
8. **release-windows**: Release do .exe Windows (apenas em tags)

### Job Auto-PR (create-pr)

- Dispara em: qualquer push em branches que não seja `main`
- Verifica se PR já existe
- Cria novo PR automaticamente se não existir
- Usa o PAT para autenticação

## Teste

```bash
git checkout -b feature/teste
git push -u origin feature/teste
```

Verifique a aba Actions no GitHub para ver o workflow executando e o PR sendo criado.
