import java.util.ArrayList;
import java.util.List;

public class ArvoreB {

    private int ordem;
    private int minChaves;
    private NoB raiz;

    private class NoB {
        List<Integer> chaves;
        List<NoB> filhos;
        boolean folha;

        NoB(boolean folha) {
            this.folha = folha;
            this.chaves = new ArrayList<>();
            this.filhos = new ArrayList<>();
        }
    }

    public ArvoreB(int ordem) {
        this.ordem = ordem;
        this.minChaves = (int) Math.ceil(ordem / 2.0) - 1;
        this.raiz = new NoB(true);
    }

    public void inserir(int chave) {
        NoB r = raiz;
        if (r.chaves.size() == ordem - 1) {
            NoB novaRaiz = new NoB(false);
            raiz = novaRaiz;
            novaRaiz.filhos.add(r);
            dividirFilho(novaRaiz, 0);
            inserirNaoCheio(novaRaiz, chave);
        } else {
            inserirNaoCheio(r, chave);
        }
    }

    private void inserirNaoCheio(NoB no, int chave) {
        int i = no.chaves.size() - 1;
        if (no.folha) {
            no.chaves.add(0);
            while (i >= 0 && chave < no.chaves.get(i)) {
                no.chaves.set(i + 1, no.chaves.get(i));
                i--;
            }
            no.chaves.set(i + 1, chave);
        } else {
            while (i >= 0 && chave < no.chaves.get(i))
                i--;
            i++;
            if (no.filhos.get(i).chaves.size() == ordem - 1) {
                dividirFilho(no, i);
                if (chave > no.chaves.get(i))
                    i++;
            }
            inserirNaoCheio(no.filhos.get(i), chave);
        }
    }

    private void dividirFilho(NoB pai, int i) {
        NoB cheio = pai.filhos.get(i);
        int meio = (ordem - 1) / 2;
        NoB novo = new NoB(cheio.folha);

        pai.chaves.add(i, cheio.chaves.get(meio));
        pai.filhos.add(i + 1, novo);

        novo.chaves.addAll(cheio.chaves.subList(meio + 1, cheio.chaves.size()));

        if (!cheio.folha)
            novo.filhos.addAll(cheio.filhos.subList(meio + 1, cheio.filhos.size()));

        int total = cheio.chaves.size();
        for (int j = total - 1; j >= meio; j--)
            cheio.chaves.remove(j);

        if (!cheio.folha) {
            int totalF = cheio.filhos.size();
            for (int j = totalF - 1; j > meio; j--)
                cheio.filhos.remove(j);
        }
    }

    public void remover(int chave) {
        if (raiz.chaves.isEmpty()) {
            System.out.println("Arvore vazia.");
            return;
        }
        String caso = removerAux(raiz, chave);
        System.out.println("Remocao " + chave + ": " + caso);

        if (raiz.chaves.isEmpty() && !raiz.folha)
            raiz = raiz.filhos.get(0);
    }

    private String removerAux(NoB no, int chave) {
        int idx = acharIndice(no, chave);

        if (idx < no.chaves.size() && no.chaves.get(idx) == chave) {
            if (no.folha) {
                no.chaves.remove(idx);
                return "folha sem underflow";
            } else {
                return removerInterno(no, idx);
            }
        } else {
            if (no.folha)
                return "chave nao encontrada";

            boolean ultimo = (idx == no.chaves.size());
            NoB filho = no.filhos.get(idx);

            if (filho.chaves.size() <= minChaves)
                preencher(no, idx);

            if (ultimo && idx > no.chaves.size())
                return removerAux(no.filhos.get(idx - 1), chave);
            else
                return removerAux(no.filhos.get(idx), chave);
        }
    }

    private String removerInterno(NoB no, int idx) {
        int chave = no.chaves.get(idx);
        NoB filhoEsq = no.filhos.get(idx);
        NoB filhoDir = no.filhos.get(idx + 1);

        if (filhoEsq.chaves.size() > minChaves) {
            int pred = pegarPredecessor(filhoEsq);
            no.chaves.set(idx, pred);
            removerAux(filhoEsq, pred);
            return "no interno - substituto (predecessor): " + pred;
        } else if (filhoDir.chaves.size() > minChaves) {
            int suc = pegarSucessor(filhoDir);
            no.chaves.set(idx, suc);
            removerAux(filhoDir, suc);
            return "no interno - substituto (sucessor): " + suc;
        } else {
            fundir(no, idx);
            return removerAux(filhoEsq, chave) + " (apos merge)";
        }
    }

    private int pegarPredecessor(NoB no) {
        while (!no.folha)
            no = no.filhos.get(no.filhos.size() - 1);
        return no.chaves.get(no.chaves.size() - 1);
    }

    private int pegarSucessor(NoB no) {
        while (!no.folha)
            no = no.filhos.get(0);
        return no.chaves.get(0);
    }

    private int acharIndice(NoB no, int chave) {
        int idx = 0;
        while (idx < no.chaves.size() && no.chaves.get(idx) < chave)
            idx++;
        return idx;
    }

    private void preencher(NoB no, int idx) {
        if (idx > 0 && no.filhos.get(idx - 1).chaves.size() > minChaves) {
            emprestarDoAnterior(no, idx);
        } else if (idx < no.chaves.size() && no.filhos.get(idx + 1).chaves.size() > minChaves) {
            emprestarDoProximo(no, idx);
        } else {
            if (idx < no.chaves.size())
                fundir(no, idx);
            else
                fundir(no, idx - 1);
        }
    }

    private void emprestarDoAnterior(NoB no, int idx) {
        NoB filho = no.filhos.get(idx);
        NoB irmao = no.filhos.get(idx - 1);

        filho.chaves.add(0, no.chaves.get(idx - 1));
        no.chaves.set(idx - 1, irmao.chaves.remove(irmao.chaves.size() - 1));

        if (!irmao.folha)
            filho.filhos.add(0, irmao.filhos.remove(irmao.filhos.size() - 1));
    }

    private void emprestarDoProximo(NoB no, int idx) {
        NoB filho = no.filhos.get(idx);
        NoB irmao = no.filhos.get(idx + 1);

        filho.chaves.add(no.chaves.get(idx));
        no.chaves.set(idx, irmao.chaves.remove(0));

        if (!irmao.folha)
            filho.filhos.add(irmao.filhos.remove(0));
    }

    private void fundir(NoB no, int idx) {
        NoB esq = no.filhos.get(idx);
        NoB dir = no.filhos.get(idx + 1);

        esq.chaves.add(no.chaves.remove(idx));
        esq.chaves.addAll(dir.chaves);

        if (!esq.folha)
            esq.filhos.addAll(dir.filhos);

        no.filhos.remove(idx + 1);
    }

    public void imprimir() {
        imprimirAux(raiz, "", true);
        System.out.println();
    }

    private void imprimirAux(NoB no, String prefixo, boolean ultimo) {
        System.out.print(prefixo);
        if (ultimo) {
            System.out.print("+-- ");
            prefixo += "    ";
        } else {
            System.out.print("|-- ");
            prefixo += "|   ";
        }
        System.out.println(no.chaves);

        if (!no.folha) {
            for (int i = 0; i < no.filhos.size(); i++)
                imprimirAux(no.filhos.get(i), prefixo, i == no.filhos.size() - 1);
        }
    }

    public static void main(String[] args) {
        ArvoreB arvore = new ArvoreB(4);

        int[] inserir = {1, 3, 7, 10, 11, 13, 14, 15, 18, 16, 19, 24, 25, 26, 21, 4, 5, 20, 22, 2, 17, 12, 6};
        System.out.println("Inserindo valores...");
        for (int v : inserir)
            arvore.inserir(v);

        System.out.println("Arvore apos insercoes:");
        arvore.imprimir();

        int[] remover = {6, 13, 7, 4, 2, 16};
        for (int r : remover) {
            System.out.println("--- Removendo " + r + " ---");
            arvore.remover(r);
            System.out.println("Arvore:");
            arvore.imprimir();
        }
    }
}
