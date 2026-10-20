# Entrega das recompensas — runbook

**Economia de Inovação GAB.** Complementa [`GAMIFICACAO.md`](./GAMIFICACAO.md) §5.

O app reserva Asas e estoque no momento do pedido. Quem passa nas regras públicas do catálogo (saldo, estoque, nível) entra já `APROVADO`. A recompensa só existe de verdade quando uma área do grupo entrega o benefício.

## Prazos no app

| Status | O que o colaborador vê | O que alguém precisa fazer |
|--------|------------------------|----------------------------|
| `APROVADO` | "Resgate confirmado. A entrega é combinada com RH, garagem ou a área parceira." | A área da tabela abaixo agenda a entrega. |
| `ENTREGUE` | "Entregue." | Líder marca entregue no app **depois** que a pessoa recebeu. |
| `SOLICITADO` | Mesmo texto de `APROVADO` (pedido legado). | Marcar entregue; não há mais veto. |
| `NEGADO` | Pedidos antigos: "As Asas voltaram." | Nada a entregar. Novos pedidos não podem ser negados. |

Não há fila de aprovação pessoa a pessoa. O catálogo é a regra.

## Quem entrega o quê

| Item do catálogo inicial | Área responsável | Como entregar |
|--------------------------|------------------|---------------|
| Passagem rodoviária Águia Branca | Divisão Passageiros / guichê da base | Emitir ida e volta para o colaborador ou um familiar, em linha da Viação Águia Branca |
| 30% na revisão do carro | Divisão Comércio (oficina da concessionária) | Autorizar o desconto no CPF do colaborador |
| Jogo de pneus Michelin | Divisão Comércio / rede Michelin do grupo | Condição de colaborador, uma vez por resgate |
| Day-off de reconhecimento | RH + gestor direto | Agendar um dia de folga remunerada |
| Vale-compras R$ 100 | RH / parceiro de varejo | Crédito ou voucher de R$ 100 |
| Trilha de capacitação | Universidade Corporativa GAB | Vaga em curso de liderança, segurança ou tecnologia |
| Almoço com a diretoria | Holding / diretoria da divisão | Uma agenda com a pessoa que teve a ideia |
| Vaga no Demoday InovaGAB | Comitê do Prêmio InovaGAB | Incluir o pitch na edição do Demoday, com mentoria antes |

O Líder pode criar outros itens. Ao criar, registra no campo parceiro **quem** entrega, para o runbook não ficar só no catálogo inicial.

## O que o app não faz

- Não emite passagem, voucher nem folga.
- Não avisa o RH por e-mail.
- Não marca `ENTREGUE` sozinho.

`ENTREGUE` no app significa que o Líder confirmou a entrega combinada acima. Sem essa confirmação, o colaborador continua vendo o pedido como confirmado e ainda não recebido.
