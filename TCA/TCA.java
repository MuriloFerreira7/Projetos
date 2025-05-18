package TCA;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class TCA {
    final static Scanner LER = new Scanner(System.in);
    final static Random R = new Random();
    final static String CAMINHO_MAIOR_PONTUACAO = "C:\\Users\\Murilovski\\Documents\\RepositorioGitHub\\Projetos\\TCA\\MaiorPontuacao.txt";
    final static String CAMINHO_SOM_DERROTA = "C:\\Users\\Murilovski\\Documents\\RepositorioGitHub\\Projetos\\2048\\sounds\\Lose-sound-effects.wav";
    final static String CAMINHO_SOM_VITORIA = "C:\\Users\\Murilovski\\Documents\\RepositorioGitHub\\Projetos\\TCA\\sounds\\You-Win-_-Street-Fighter-_-Sound-Effect.wav";

    public static void main(String[] args) throws InterruptedException {
        imprimirCabecalho();
        Thread.sleep(1000);
        limparTela();
        final int[] TAMANHO_DO_CAMPO = { 5, 4 };
        int[][] campo = criarMatriz(TAMANHO_DO_CAMPO[0], TAMANHO_DO_CAMPO[1]);// [4][0] = pontos, [4][1] =
                                                                              // maiorPontuação
        boolean[][] campoB = criarMatrizB(TAMANHO_DO_CAMPO[0], TAMANHO_DO_CAMPO[1]);
        campoB = inicializarMatrizB(campoB, true);
        int[][] guardarCampo = new int[campo.length][campo[0].length];
        boolean perdeu = false;
        boolean ganhou = false;
        boolean primeiraVitoria = true;
        char movimento = 0;
        char continuarJogo = 0;
        int valor = 0;// valor adicionado aleatoriamente no campo
        campo[campo.length - 1][1] = lerArquivo(CAMINHO_MAIOR_PONTUACAO);
        // escreverArquivo("0");
        // campo[0][0] = 1024; campo[0][1] = 1024;
        // campo[3][0] = 2; campo[3][1] = 2; campo[3][2] = 2; campo[3][3] = 2;
        // campo[0][1] = 2; campo[0][2] = 2; campo[0][3] = 2;
        while (true) {
            valor = R.nextInt(5);
            if (valor == 0) {
                valor = 4;
            } else {
                valor = 2;
            }
            campo = adicionarValor(campo, valor);
            campoB = inicializarMatrizB(campoB, true);
            limparTela();//
            imprimir("pontuação: " + campo[campo.length - 1][0] + " | ");
            imprimir("Maior pontuação: " + campo[campo.length - 1][1] + "\n\n");
            imprimirCampo(campo);
            imprimirInstrucoes();
            movimento = lerMovimento();
            do {
                guardarCampo = copiarMatriz(campo);
                campo = moverCampo(campo, movimento, campoB);
            } while (!verificarIgualdade(campo, guardarCampo));

            if (campo[campo.length - 1][0] > campo[campo.length - 1][1]) {
                campo[campo.length - 1][1] = campo[campo.length - 1][0];
                escreverArquivo(campo[campo.length - 1][0] + "");
            }

            // campo[0][0] = 2; campo[0][1] = 4; campo[0][2] = 2; campo[0][3] = 4;
            // campo[1][0] = 4; campo[1][1] = 2; campo[1][2] = 4; campo[1][3] = 2;
            // campo[2][0] = 2; campo[2][1] = 4; campo[2][2] = 2; campo[2][3] = 4;
            // campo[3][0] = 4; campo[3][1] = 2; campo[3][2] = 4; campo[3][3] = 2;
            // imprimirCampo(campo);
            perdeu = verificarSePerdeu(campo, campoB);
            if (perdeu) {
                tocarSom(CAMINHO_SOM_DERROTA);
                imprimir("Mais sorte na próxima\n");
                break;
            }

            ganhou = verificarVitoria(campo);
            if (ganhou && primeiraVitoria) {
                imprimir("Parabéns Você conseguiu atingir o objetivo do jogo. Sua pontuação final foi de "
                        + campo[campo.length - 1][0]);
                tocarSom(CAMINHO_SOM_VITORIA);
                imprimir("\n" + "você deseja continuar o jogo?(sim ou nao): ");
                continuarJogo = LER.next().toLowerCase().charAt(0);
                if (continuarJogo == 'n') {
                    break;
                }
                primeiraVitoria = false;
            }
        }
        imprimir("FIM DE JOGO!\n");
    }

    public static boolean[][] criarMatrizB(int n, int m) {
        boolean[][] matriz = new boolean[n][m];
        return matriz;
    }

    public static boolean[][] inicializarMatrizB(boolean[][] matriz, boolean bool) {
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                matriz[i][j] = bool;
            }
        }

        return matriz;
    }

    public static boolean[][] copiarMatrizB(boolean[][] matriz) {
        boolean[][] copiaMatriz = new boolean[matriz.length][matriz[0].length];
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[0].length; j++) {
                copiaMatriz[i][j] = matriz[i][j];
            }
        }

        return copiaMatriz;
    }

    public static int lerArquivo(String caminhoArquivo) {
        int valor = 0;
        try {
            File meuArquivo = new File(caminhoArquivo);
            Scanner LERARQUIVO = new Scanner(meuArquivo);
            valor = Integer.parseInt(LERARQUIVO.nextLine());
            LERARQUIVO.close();
        } catch (Exception e) {
            return -1;
        }
        return valor;
    }

    public static void escreverArquivo(String maiorPontuacao) {
        try {
            FileWriter escrita = new FileWriter(CAMINHO_MAIOR_PONTUACAO, false); // Sobrescreve o arquivo
            escrita.write(maiorPontuacao);
            escrita.close();
        } catch (Exception e) {
            System.out.println("Erro ao escrever o arquivo");
            e.printStackTrace();
        }
    }

    public static void imprimirCabecalho() {
        System.out.println("\033[0;31m" + "TCA 2024");
        System.out.println("\033[0;30m" + "Dev by Murilo Ferreira");
        System.out.println("\033[0;34m" + "Orientado por: Odair Moreira de Souza, Thiago Berticelli Ló");
        System.out.println("\033[0;36m" + "Colaboradores: Beatriz e Vinicius");
        System.out.println("\033[0;33m" + "Beta testers: Roberto Brittes e Felipe Chaves");
        System.out.println("\033[0;35m" + "Bom Jogo");
        System.out.print("\033[0m");
    }

    public static void limparTela() {
        for (int i = 0; i < 40; ++i) {
            System.out.println();
        }
        System.out.print("\033\143");

    }

    public static void tocarSom(String caminhoArquivoSom) {
        try {
            // Abrindo o arquivo de som
            File soundFile = new File(caminhoArquivoSom);
            if (!soundFile.exists()) {
                System.out.println("Arquivo de som não encontrado: " + caminhoArquivoSom);
                return;
            }

            // Criando um AudioInputStream
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);

            // Obtendo as informações do formato de áudio
            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);

            // Criando o Clip e carregando o áudio
            Clip audioClip = (Clip) AudioSystem.getLine(info);
            audioClip.open(audioStream);

            // Tocando o áudio
            audioClip.start();

            // Mantendo o programa ativo enquanto o som toca
            Thread.sleep(audioClip.getMicrosecondLength() / 700);

            // Liberando recursos
            audioClip.close();
            audioStream.close();
        } catch (UnsupportedAudioFileException e) {
            System.err.println("O formato de áudio não é suportado.");
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            System.err.println("Linha de áudio não disponível.");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo de áudio.");
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("O programa foi interrompido.");
            e.printStackTrace();
        }
    }

    public static void imprimir(String texto) {
        System.out.printf(texto);
    }

    public static boolean verificarVitoria(int[][] campo) {
        boolean venceu = false;
        for (int i = 0; i < campo.length - 1; i++) {
            for (int j = 0; j < campo[i].length; j++) {
                if (campo[i][j] >= 2048) {
                    venceu = true;
                }
            }
        }

        return venceu;
    }

    public static boolean verificarSePerdeu(int[][] campo, boolean[][] campoB) {// faço a movimentação para os 4 lados
                                                                                // possiveis, se depois
        // de todas as movimentações o campo permanecer o mesmo,
        // significa que o usuario perdeu
        int[][] campo1 = copiarMatriz(campo);// duplicação de campo pra não usar o mesmo espaço de memoria
        int[][] guardarCampo = copiarMatriz(campo1);
        boolean[][] copiaCampoB = copiarMatrizB(campoB);
        boolean[] possivelMovimentar = new boolean[4];// 0-esquerda 1-direita 2-cima 4-baixo
        char[] movimentos = { 'a', 'd', 'w', 's' };
        boolean perdeu = true;

        for (int k = 0; k < possivelMovimentar.length; k++) {
            campo1 = moverCampo(campo1, movimentos[k], copiaCampoB);
            if (verificarIgualdade(guardarCampo, campo1)) {
                possivelMovimentar[k] = false;
            } else {
                possivelMovimentar[k] = true;
            }
            campo1 = copiarMatriz(guardarCampo);
        }

        for (int i = 0; i < possivelMovimentar.length; i++) {
            if (possivelMovimentar[i] == true) {
                perdeu = false;
                break;
            }
        }

        return perdeu;
    }

    public static int[][] copiarMatriz(int[][] matriz) {
        int[][] matrizCopiada = new int[matriz.length][matriz[0].length];
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                matrizCopiada[i][j] = matriz[i][j];
            }
        }

        return matrizCopiada;
    }

    public static boolean verificarIgualdade(int[][] guardarCampo, int[][] campo) {// retorna true se as duas matrizes
                                                                                   // que entraram forem iguais
        boolean iguais = true;

        for (int i = 0; i < campo.length - 2; i++) {
            for (int j = 0; j < campo[i].length; j++) {
                if (guardarCampo[i][j] != campo[i][j]) {
                    iguais = false;
                    break;
                }
            }
        }

        return iguais;
    }

    public static int[][] moverCampo(int[][] campo, char movimento, boolean[][] campoB) {

        switch (movimento) {
            case 'a':// esquerda
                moverCampoEsquerda(campo, campoB);
                break;

            case 'd':// direita
                moverCampoDireita(campo, campoB);
                break;

            case 'w':// cima
                moverCampoCima(campo, campoB);
                break;

            case 's':// baixo
                moverCampoBaixo(campo, campoB);
                break;
        }
        return campo;
    }

    public static int[][] moverCampoBaixo(int[][] campo, boolean[][] campoB) {
        for (int i = 0; i < campo.length - 1; i++) {
            for (int j = 0; j < campo[i].length; j++) {
                if (i == campo.length - 2) {
                    break;
                } else if (campo[i][j] == campo[i + 1][j] && campoB[i][j] && campoB[i + 1][j]
                        && verificarPreferenciaBaixo(i, j, campo)) {
                    campo[i + 1][j] += campo[i][j];
                    campo[i][j] = 0;
                    campo[campo.length - 1][0] += campo[i + 1][j];
                    campoB[i + 1][j] = false;
                } else if (campo[i + 1][j] == 0 && campo[i][j] != 0) {
                    campo[i + 1][j] = campo[i][j];
                    campo[i][j] = 0;
                    campoB[i + 1][j] = campoB[i][j];
                    campoB[i][j] = true;
                }
            }
        }

        return campo;
    }

    public static boolean verificarPreferenciaBaixo(int i, int j, int[][] campo) {
        boolean preferencia = true;
        int contador = 0;

        for (int k = i + 1; k < campo[j].length; k++) {
            if (campo[i][j] == campo[k][j]) {
                contador++;
            }
            if (campo[i][j] != campo[k][j]) {
                contador = 0;
            }
            if (contador >= 2) {
                preferencia = false;
                break;
            }
        }

        return preferencia;
    }

    public static int[][] moverCampoCima(int[][] campo, boolean[][] campoB) {
        for (int i = campo.length - 2; i >= 0; i--) {
            for (int j = campo[i].length - 1; j >= 0; j--) {
                if (i == 0) {
                    break;
                } else if (campo[i][j] == campo[i - 1][j] && campoB[i][j] && campoB[i - 1][j]
                        && verificarPreferenciaCima(i, j, campo)) {
                    campo[i - 1][j] += campo[i][j];
                    campo[i][j] = 0;
                    campo[campo.length - 1][0] += campo[i - 1][j];
                    campoB[i - 1][j] = false;
                } else if (campo[i - 1][j] == 0 && campo[i][j] != 0) {
                    campo[i - 1][j] = campo[i][j];
                    campo[i][j] = 0;
                    campoB[i - 1][j] = campoB[i][j];
                    campoB[i][j] = true;
                }
            }
        }

        return campo;
    }

    public static boolean verificarPreferenciaCima(int i, int j, int[][] campo) {
        boolean preferencia = true;
        int contador = 0;

        for (int k = i - 1; k >= 0; k--) {
            if (campo[i][j] == campo[k][j]) {
                contador++;
            }
            if (campo[i][j] != campo[k][j]) {
                contador = 0;
            }
            if (contador >= 2) {
                preferencia = false;
                break;
            }
        }

        return preferencia;
    }

    public static int[][] moverCampoDireita(int[][] campo, boolean[][] campoB) {
        for (int i = 0; i < campo.length - 1; i++) {
            for (int j = 0; j < campo[i].length; j++) {
                if (j == campo[i].length - 1) {
                    break;
                } else if (campo[i][j] == campo[i][j + 1] && campoB[i][j] && campoB[i][j + 1]
                        && verificarPreferenciaDireita(i, j, campo)) {
                    campo[i][j + 1] += campo[i][j];
                    campo[i][j] = 0;
                    campo[campo.length - 1][0] += campo[i][j + 1];
                    campoB[i][j + 1] = false;
                } else if (campo[i][j + 1] == 0 && campo[i][j] != 0) {
                    campo[i][j + 1] = campo[i][j];
                    campo[i][j] = 0;
                    campoB[i][j + 1] = campoB[i][j];
                    campoB[i][j] = true;
                }
            }
        }

        return campo;
    }

    public static boolean verificarPreferenciaDireita(int i, int j, int[][] campo) {
        boolean preferencia = true;
        int contador = 0;

        for (int k = j + 1; k < campo[i].length; k++) {
            if (campo[i][j] == campo[i][k]) {
                contador++;
            }
            if (campo[i][j] != campo[i][k]) {
                contador = 0;
            }
            if (contador >= 2) {
                preferencia = false;
                break;
            }
        }

        return preferencia;
    }

    public static int[][] moverCampoEsquerda(int[][] campo, boolean[][] campoB) {
        for (int i = campo.length - 2; i >= 0; i--) {
            for (int j = campo[i].length - 1; j >= 0; j--) {
                if (j == 0) {
                    break;
                } else if (campo[i][j] == campo[i][j - 1] && campoB[i][j] && campoB[i][j - 1]
                        && verificarPreferenciaEsquerda(i, j, campo)) {
                    campo[i][j - 1] += campo[i][j];
                    campo[i][j] = 0;
                    campo[campo.length - 1][0] += campo[i][j - 1];
                    campoB[i][j - 1] = false;
                } else if (campo[i][j - 1] == 0 && campo[i][j] != 0) {
                    campo[i][j - 1] = campo[i][j];
                    campo[i][j] = 0;
                    campoB[i][j - 1] = campoB[i][j];
                    campoB[i][j] = true;
                }
            }
        }

        return campo;
    }

    public static boolean verificarPreferenciaEsquerda(int i, int j, int[][] campo) {
        boolean preferencia = true;
        int contador = 0;

        for (int k = j - 1; k >= 0; k--) {
            if (campo[i][j] == campo[i][k]) {
                contador++;
            }
            if (campo[i][j] != campo[i][k]) {
                contador = 0;
            }
            if (contador >= 2) {
                preferencia = false;
                break;
            }
        }

        return preferencia;
    }

    public static int lerNumeroInteiro() {
        return LER.nextInt();
    }

    public static char lerMovimento() {
        String valor = null;
        char movimento = 0;
        boolean naoCaracter = false;

        do {
            valor = LER.next().toLowerCase();

            if (valor.length() > 1) {
                System.out.println("Somente uma letra");
                naoCaracter = true;
            } else {
                movimento = valor.charAt(0);
                naoCaracter = false;
            }

            if ((movimento != 'w' && movimento != 'a' && movimento != 's' && movimento != 'd') && !naoCaracter) {
                System.out.println("VALOR INVÁLIDO!");
            }

        } while ((movimento != 'w' && movimento != 'a' && movimento != 's' && movimento != 'd') || valor.length() > 1);

        return valor.charAt(0);
    }

    public static void imprimirInstrucoes() {
        System.out.printf("Esquerda-a\nDireita-d\nCima-w\nBaixo-s\n\n");
    }

    public static int[][] adicionarValor(int[][] campo, int valor) {// metodo que eu randomizo uma posição do campo que
                                                                    // n tem nada e adiciono um valor lá
        int[] posicoes = randomizarPosicao(campo.length - 1, campo);
        campo[posicoes[0]][posicoes[1]] = valor;
        return campo;
    }

    public static int[][] criarMatriz(int n, int m) {
        int[][] matriz = new int[n][m];
        return matriz;
    }

    public static int[] randomizarPosicao(int tamanho, int[][] campo) {
        int[] posicoes = new int[2];
        boolean temValor = false;
        do {
            for (int i = 0; i < posicoes.length; i++) {
                posicoes[i] = R.nextInt(tamanho);
            }
            temValor = verificarSeTemValor(posicoes, campo);
        } while (temValor);
        return posicoes;
    }

    public static boolean verificarSeTemValor(int[] posicoes, int[][] campo) {
        boolean temValor = false;

        if (campo[posicoes[0]][posicoes[1]] != 0) {
            temValor = true;
        }

        return temValor;
    }

    public static void imprimirCampo(int[][] matriz) {
        String cor = null;

        for (int i = 0; i < matriz.length - 1; i++) {
            for (int j = 0; j < matriz[0].length; j++) {
                cor = definirCor(matriz[i][j]);
                System.out.printf(cor + "%5d", matriz[i][j]);
            }
            System.out.println("\n");
        }

        cor = "\033[0m";// cor padrão
        System.out.println(cor);
    }

    public static String definirCor(int valor) {
        String cor = null;
        switch (valor) {
            case 0:
                cor = "\033[0m";// cor padrão
                break;

            case 2:
                cor = "\033[0;30m";// Preto
                break;

            case 4:
                cor = "\033[0;33m";// Amarelo
                break;

            case 8:
                cor = "\033[0;34m";// Azul
                break;

            case 16:
                cor = "\033[0;32m";// Verde
                break;

            case 32:
                cor = "\033[0;31m";// vermelho
                break;

            case 64:
                cor = "\033[0;35m";// roxo
                break;

            case 128:
                cor = "\033[0;36m";// Ciano
                break;

            case 256:
                cor = "\033[0;34m";// Azul;
                break;

            case 512:
                cor = "\033[0;32m";// Verde
                break;

            case 1024:
                cor = "\033[0;31m";// vermelho
                break;

            case 2048:
                cor = "\033[0;35m";// roxo
                break;

            default:
                cor = "\033[0;36m";// Ciano
                break;
        }
        return cor;
    }
}