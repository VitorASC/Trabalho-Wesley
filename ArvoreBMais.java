import java.util.ArrayList;
import java.util.List;

public class ArvoreBMais {

    private int ordem;
    private int minChaves;
    private NoBMais raiz;

    private class NoBMais {
        List<Integer> chaves;
        List<NoBMais> filhos;
        NoBMais proximo;
        boolean folha;

        NoBMais(boolean folha) {
            this.folha = folha;
            this.chaves = new ArrayList<>();
            this.filhos = new ArrayList<>();
            this.proximo = null;
        }
    }

    public ArvoreBMais(int ordem) {
        this.ordem = ordem;
        this.minChaves = (int) Math.ceil(ordem / 2.0) - 1;
        this.raiz = new NoBMais(true);
    }

    public void inserir(int chave) {
        NoBMais r = raiz;
        if (r.chaves.size() == ordem - 1) {
            NoBMais novaRaiz = new NoBMais(false);
            novaRaiz.filhos.add(r);
            dividirFilho(novaRaiz, 0);
            raiz = novaRaiz;
            inserirNaoCheio(raiz, chave);
        } else {
            inserirNaoCheio(r, chave);
        }
    }

    private void inserirNaoCheio(NoBMais no, int chave) {
        if (no.folha) {
            int i = 0;
            while (i < no.chaves.size() && no.chaves.get(i) < chave)
                i++;
            no.chaves.add(i, chave);
        } else {
            int i = no.chaves.size() - 1;
            while (i >= 0 && chave < no.chaves.get(i))
                i--;
            i++;
            NoBMais filho = no.filhos.get(i);
            if (filho.chaves.size() == ordem - 1) {
                dividirFilho(no, i);
                if (chave > no.chaves.get(i))
                    i++;
            }
            inserirNaoCheio(no.filhos.get(i), chave);
        }
    }

    private void dividirFilho(NoBMais pai, int i) {
        NoBMais cheio = pai.filhos.get(i);
        NoBMais novo = new NoBMais(cheio.folha);
        int meio = (ordem - 1) / 2;

        if (cheio.folha) {
            novo.chaves.addAll(cheio.chaves.subList(meio, cheio.chaves.size()));
            for (int j = cheio.chaves.size() - 1; j >= meio; j--)
                cheio.chaves.remove(j);

            pai.chaves.add(i, novo.chaves.get(0));
            pai.filhos.add(i + 1, novo);

            novo.proximo = cheio.proximo;
            cheio.proximo = novo;
        } else {
            int chaveSobe = cheio.chaves.get(meio);
            novo.chaves.addAll(cheio.chaves.subList(meio + 1, cheio.chaves.size()));
            novo.filhos.addAll(cheio.filhos.subList(meio + 1, cheio.filhos.size()));

            int total = cheio.chaves.size();
            for (int j = total - 1; j >= meio; j--)
                cheio.chaves.remove(j);
            int totalF = cheio.filhos.size();
            for (int j = totalF - 1; j > meio; j--)
                cheio.filhos.remove(j);

            pai.chaves.add(i, chaveSobe);
            pai.filhos.add(i + 1, novo);
        }
    }

    public void remover(int chave) {
        removerAux(raiz, chave);
        if (raiz.chaves.isEmpty() && !raiz.folha)
            raiz = raiz.filhos.get(0);
    }

    private void removerAux(NoBMais no, int chave) {
        if (no.folha) {
            no.chaves.remove(Integer.valueOf(chave));
            return;
        }

        int idx = 0;
        while (idx < no.chaves.size() && chave >= no.chaves.get(idx))
            idx++;

        NoBMais filho = no.filhos.get(idx);
        removerAux(filho, chave);

        if (idx > 0 && !filho.chaves.isEmpty()) {
            if (no.chaves.get(idx - 1) == chave) {
                int menorChave = pegarMenor(filho);
                no.chaves.set(idx - 1, menorChave);
            }
        }

        if (filho.chaves.size() < minChaves)
            tratarUnderflow(no, idx);

        atualizarChavesInternas(no);
    }

    private int pegarMenor(NoBMais no) {
        while (!no.folha)
            no = no.filhos.get(0);
        return no.chaves.get(0);
    }

    private void atualizarChavesInternas(NoBMais no) {
        if (no.folha) return;
        for (int i = 1; i < no.filhos.size(); i++) {
            if (i - 1 < no.chaves.size()) {
                int menor = pegarMenor(no.filhos.get(i));
                no.chaves.set(i - 1, menor);
            }
        }
    }

    private void tratarUnderflow(NoBMais pai, int idx) {
        NoBMais filho = pai.filhos.get(idx);

        if (idx > 0) {
            NoBMais irmaoEsq = pai.filhos.get(idx - 1);
            if (irmaoEsq.chaves.size() > minChaves) {
                redistribuirDaEsquerda(pai, idx);
                return;
            }
        }

        if (idx < pai.filhos.size() - 1) {
            NoBMais irmaoDir = pai.filhos.get(idx + 1);
            if (irmaoDir.chaves.size() > minChaves) {
                redistribuirDaDireita(pai, idx);
                return;
            }
        }

        if (idx > 0)
            fundirNos(pai, idx - 1);
        else
            fundirNos(pai, idx);
    }

    private void redistribuirDaEsquerda(NoBMais pai, int idx) {
        NoBMais filho = pai.filhos.get(idx);
        NoBMais irmao = pai.filhos.get(idx - 1);

        if (filho.folha) {
            int emprestada = irmao.chaves.remove(irmao.chaves.size() - 1);
            filho.chaves.add(0, emprestada);
            pai.chaves.set(idx - 1, filho.chaves.get(0));
        } else {
            filho.chaves.add(0, pai.chaves.get(idx - 1));
            pai.chaves.set(idx - 1, irmao.chaves.remove(irmao.chaves.size() - 1));
            if (!irmao.filhos.isEmpty())
                filho.filhos.add(0, irmao.filhos.remove(irmao.filhos.size() - 1));
        }
    }

    private void redistribuirDaDireita(NoBMais pai, int idx) {
        NoBMais filho = pai.filhos.get(idx);
        NoBMais irmao = pai.filhos.get(idx + 1);

        if (filho.folha) {
            int emprestada = irmao.chaves.remove(0);
            filho.chaves.add(emprestada);
            pai.chaves.set(idx, irmao.chaves.get(0));
        } else {
            filho.chaves.add(pai.chaves.get(idx));
            pai.chaves.set(idx, irmao.chaves.remove(0));
            if (!irmao.filhos.isEmpty())
                filho.filhos.add(irmao.filhos.remove(0));
        }
    }

    private void fundirNos(NoBMais pai, int idx) {
        NoBMais esq = pai.filhos.get(idx);
        NoBMais dir = pai.filhos.get(idx + 1);

        if (!esq.folha)
            esq.chaves.add(pai.chaves.get(idx));

        esq.chaves.addAll(dir.chaves);

        if (!esq.folha)
            esq.filhos.addAll(dir.filhos);

        if (esq.folha)
            esq.proximo = dir.proximo;

        pai.chaves.remove(idx);
        pai.filhos.remove(idx + 1);
    }

    public List<Integer> consultaFaixa(int inicio, int fim) {
        List<Integer> resultado = new ArrayList<>();
        NoBMais folha = encontrarFolha(inicio);

        while (folha != null) {
            for (int chave : folha.chaves) {
                if (chave >= inicio && chave <= fim)
                    resultado.add(chave);
                if (chave > fim)
                    return resultado;
            }
            folha = folha.proximo;
        }
        return resultado;
    }

    private NoBMais encontrarFolha(int chave) {
        NoBMais no = raiz;
        while (!no.folha) {
            int i = 0;
            while (i < no.chaves.size() && chave >= no.chaves.get(i))
                i++;
            no = no.filhos.get(i);
        }
        return no;
    }

    public void imprimir() {
        imprimirAux(raiz, "", true);
        System.out.println();
    }

    private void imprimirAux(NoBMais no, String prefixo, boolean ultimo) {
        System.out.print(prefixo);
        if (ultimo) {
            System.out.print("+-- ");
            prefixo += "    ";
        } else {
            System.out.print("|-- ");
            prefixo += "|   ";
        }
        System.out.print(no.chaves);
        if (no.folha && no.proximo != null)
            System.out.print(" -> prox:" + no.proximo.chaves);
        System.out.println(no.folha ? " (folha)" : "");

        if (!no.folha) {
            for (int i = 0; i < no.filhos.size(); i++)
                imprimirAux(no.filhos.get(i), prefixo, i == no.filhos.size() - 1);
        }
    }

    public void imprimirEncadeamento() {
        NoBMais folha = raiz;
        while (!folha.folha)
            folha = folha.filhos.get(0);

        System.out.print("Encadeamento das folhas: ");
        while (folha != null) {
            System.out.print(folha.chaves);
            if (folha.proximo != null)
                System.out.print(" -> ");
            folha = folha.proximo;
        }
        System.out.println();
    }

    public static void main(String[] args) {
        ArvoreBMais arvore = new ArvoreBMais(4);

        System.out.println("Inserindo valores de 1 a 30...");
        for (int i = 1; i <= 30; i++)
            arvore.inserir(i);

        System.out.println("\nArvore B+ apos insercoes:");
        arvore.imprimir();

        System.out.println("consultaFaixa(1, 30): " + arvore.consultaFaixa(1, 30));
        System.out.println();
        arvore.imprimirEncadeamento();

        int[] remover = {5, 10, 15, 20, 25};
        for (int r : remover) {
            System.out.println("\n--- Removendo " + r + " ---");
            arvore.remover(r);
            arvore.imprimir();
        }

        System.out.println("\nconsultaFaixa(1, 30) apos remocoes: " + arvore.consultaFaixa(1, 30));
        System.out.println();
        arvore.imprimirEncadeamento();
    }
}
