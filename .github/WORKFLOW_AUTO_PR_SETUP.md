# Configuração do Workflow Auto-PR

## Setup necessário

O workflow `auto-pr.yml` requer um Personal Access Token (PAT) para funcionar.

### Passo 1: Criar o PAT

1. Acesse: https://github.com/settings/tokens
2. Clique em "Generate new token (classic)"
3. Marque as permissões:
   - ✅ `repo`
   - ✅ `workflow`
4. Copie o token

### Passo 2: Adicionar como Secret

1. Vá para: https://github.com/Gui-Kol/Paw-Fight/settings/secrets/actions
2. Clique em "New repository secret"
3. Nome: `PAT`
4. Valor: Cole o token
5. Save

## Como funciona

- Detecta push em qualquer branch (menos main)
- Verifica se PR já existe
- Cria novo PR automaticamente se não existir

## Teste

```bash
git checkout -b feature/teste
git push -u origin feature/teste
```

Verifique a aba Actions no GitHub.
