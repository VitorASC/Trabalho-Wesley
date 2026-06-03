public class ArvoreRubroNegra {

    private static final boolean VERMELHO = true;
    private static final boolean PRETO = false;

    private class No {
        int chave;
        No esq, dir, pai;
        boolean cor;

        No(int chave) {
            this.chave = chave;
            this.cor = VERMELHO;
        }
    }

    private No raiz;
    private final No NULO;

    public ArvoreRubroNegra() {
        NULO = new No(0);
        NULO.cor = PRETO;
        NULO.esq = NULO;
        NULO.dir = NULO;
        NULO.pai = NULO;
        raiz = NULO;
    }

    public void inserir(int chave) {
        No novo = new No(chave);
        novo.esq = NULO;
        novo.dir = NULO;
        novo.pai = NULO;

        No y = NULO;
        No x = raiz;

        while (x != NULO) {
            y = x;
            if (novo.chave < x.chave)
                x = x.esq;
            else
                x = x.dir;
        }

        novo.pai = y;
        if (y == NULO)
            raiz = novo;
        else if (novo.chave < y.chave)
            y.esq = novo;
        else
            y.dir = novo;

        corrigirInsercao(novo);
    }

    private void corrigirInsercao(No z) {
        while (z.pai.cor == VERMELHO) {
            if (z.pai == z.pai.pai.esq) {
                No tio = z.pai.pai.dir;
                if (tio.cor == VERMELHO) {
                    z.pai.cor = PRETO;
                    tio.cor = PRETO;
                    z.pai.pai.cor = VERMELHO;
                    z = z.pai.pai;
                } else {
                    if (z == z.pai.dir) {
                        z = z.pai;
                        rotacaoEsquerda(z);
                    }
                    z.pai.cor = PRETO;
                    z.pai.pai.cor = VERMELHO;
                    rotacaoDireita(z.pai.pai);
                }
            } else {
                No tio = z.pai.pai.esq;
                if (tio.cor == VERMELHO) {
                    z.pai.cor = PRETO;
                    tio.cor = PRETO;
                    z.pai.pai.cor = VERMELHO;
                    z = z.pai.pai;
                } else {
                    if (z == z.pai.esq) {
                        z = z.pai;
                        rotacaoDireita(z);
                    }
                    z.pai.cor = PRETO;
                    z.pai.pai.cor = VERMELHO;
                    rotacaoEsquerda(z.pai.pai);
                }
            }
        }
        raiz.cor = PRETO;
    }

    private void rotacaoEsquerda(No x) {
        No y = x.dir;
        x.dir = y.esq;
        if (y.esq != NULO)
            y.esq.pai = x;
        y.pai = x.pai;
        if (x.pai == NULO)
            raiz = y;
        else if (x == x.pai.esq)
            x.pai.esq = y;
        else
            x.pai.dir = y;
        y.esq = x;
        x.pai = y;
    }

    private void rotacaoDireita(No x) {
        No y = x.esq;
        x.esq = y.dir;
        if (y.dir != NULO)
            y.dir.pai = x;
        y.pai = x.pai;
        if (x.pai == NULO)
            raiz = y;
        else if (x == x.pai.dir)
            x.pai.dir = y;
        else
            x.pai.esq = y;
        y.dir = x;
        x.pai = y;
    }

    private void transplantar(No u, No v) {
        if (u.pai == NULO)
            raiz = v;
        else if (u == u.pai.esq)
            u.pai.esq = v;
        else
            u.pai.dir = v;
        v.pai = u.pai;
    }

    private No minimo(No x) {
        while (x.esq != NULO)
            x = x.esq;
        return x;
    }

    private No buscar(No no, int chave) {
        while (no != NULO && chave != no.chave) {
            if (chave < no.chave)
                no = no.esq;
            else
                no = no.dir;
        }
        return no;
    }

    public void remover(int chave) {
        No z = buscar(raiz, chave);
        if (z == NULO) {
            System.out.println("Chave " + chave + " nao encontrada.");
            return;
        }

        No y = z;
        No x;
        boolean corOriginal = y.cor;

        if (z.esq == NULO) {
            x = z.dir;
            transplantar(z, z.dir);
        } else if (z.dir == NULO) {
            x = z.esq;
            transplantar(z, z.esq);
        } else {
            y = minimo(z.dir);
            corOriginal = y.cor;
            x = y.dir;

            if (y.pai == z) {
                x.pai = y;
            } else {
                transplantar(y, y.dir);
                y.dir = z.dir;
                y.dir.pai = y;
            }
            transplantar(z, y);
            y.esq = z.esq;
            y.esq.pai = y;
            y.cor = z.cor;
        }

        if (corOriginal == PRETO)
            corrigirRemocao(x);
    }

    private void corrigirRemocao(No x) {
        while (x != raiz && x.cor == PRETO) {
            if (x == x.pai.esq) {
                No irmao = x.pai.dir;

                // caso 1: irmao vermelho
                if (irmao.cor == VERMELHO) {
                    irmao.cor = PRETO;
                    x.pai.cor = VERMELHO;
                    rotacaoEsquerda(x.pai);
                    irmao = x.pai.dir;
                }

                // caso 2: irmao preto, dois filhos pretos
                if (irmao.esq.cor == PRETO && irmao.dir.cor == PRETO) {
                    irmao.cor = VERMELHO;
                    x = x.pai;
                } else {
                    // caso 3: filho dir do irmao preto
                    if (irmao.dir.cor == PRETO) {
                        irmao.esq.cor = PRETO;
                        irmao.cor = VERMELHO;
                        rotacaoDireita(irmao);
                        irmao = x.pai.dir;
                    }
                    // caso 4: filho dir do irmao vermelho
                    irmao.cor = x.pai.cor;
                    x.pai.cor = PRETO;
                    irmao.dir.cor = PRETO;
                    rotacaoEsquerda(x.pai);
                    x = raiz;
                }
            } else {
                No irmao = x.pai.esq;

                if (irmao.cor == VERMELHO) {
                    irmao.cor = PRETO;
                    x.pai.cor = VERMELHO;
                    rotacaoDireita(x.pai);
                    irmao = x.pai.esq;
                }

                if (irmao.dir.cor == PRETO && irmao.esq.cor == PRETO) {
                    irmao.cor = VERMELHO;
                    x = x.pai;
                } else {
                    if (irmao.esq.cor == PRETO) {
                        irmao.dir.cor = PRETO;
                        irmao.cor = VERMELHO;
                        rotacaoEsquerda(irmao);
                        irmao = x.pai.esq;
                    }
                    irmao.cor = x.pai.cor;
                    x.pai.cor = PRETO;
                    irmao.esq.cor = PRETO;
                    rotacaoDireita(x.pai);
                    x = raiz;
                }
            }
        }
        x.cor = PRETO;
    }

    public void imprimir() {
        imprimirAux(raiz, "", true);
        System.out.println();
    }

    private void imprimirAux(No no, String prefixo, boolean ultimo) {
        if (no != NULO) {
            System.out.print(prefixo);
            if (ultimo) {
                System.out.print("R---- ");
                prefixo += "   ";
            } else {
                System.out.print("L---- ");
                prefixo += "|  ";
            }
            String cor = no.cor == VERMELHO ? "VM" : "PT";
            System.out.println(no.chave + " (" + cor + ")");
            imprimirAux(no.esq, prefixo, false);
            imprimirAux(no.dir, prefixo, true);
        }
    }

    public static void main(String[] args) {
        ArvoreRubroNegra arvore = new ArvoreRubroNegra();

        int[] valores = {7, 3, 18, 10, 22, 8, 11, 26};
        System.out.println("Inserindo: 7, 3, 18, 10, 22, 8, 11, 26");
        for (int v : valores)
            arvore.inserir(v);

        System.out.println("\nArvore apos insercoes:");
        arvore.imprimir();

        int[] remocoes = {18, 11, 3};
        for (int r : remocoes) {
            System.out.println("Removendo " + r + "...");
            arvore.remover(r);
            System.out.println("Arvore apos remover " + r + ":");
            arvore.imprimir();
        }
    }
}
