import java.util.Random;

/**
 * メインの処理を行うpublicクラス
 */
public class Kadai10 {

    public static void main(String args[]) {
       Complex[] signal1 = new Complex[10];
		Random rdm = new Random();
		for(int i = 0; i < 10; i++){
			//Complex objComp = new Complex(10*rdm.nextDouble(),10*rdm.nextDouble());	//複素信号の生成
			Complex objComp = new Complex(10*rdm.nextDouble(),0);	//実数信号成分の生成
			signal1[i] = objComp;				//複素信号の作成：先頭から順に成分を追加。
		}

        System.out.println("************* 信号の表示 *************");
        for (int j = 0; j < signal1.length; j++) {
            System.out.println("f[" + j + "]=" + signal1[j].real + " + j" + signal1[j].img);
        }

        System.out.println("************* DFT後の周波数成分の表示 *************");
        Complex[] spectrum = new Complex[10];
        for (int k = 0; k < spectrum.length; k++) {
            spectrum[k] = dft(k, signal1);
            System.out.println("F[" + k + "]=" + spectrum[k].real + " + j" + spectrum[k].img);
        }

        System.out.println("************* IDFT後の信号の表示 *************");
        Complex[] spectrum2 = new Complex[10];
        for (int i = 0; i < spectrum.length; i++) {
            spectrum2[i] = idft(i, spectrum);
            System.out.println("f[" + i + "]=" + spectrum2[i].real + " + j" + spectrum2[i].img);
        }
    }

    /**
     * k番目の1次元DFTの結果を返すメソッド
     */
    public static Complex dft(int k, Complex[] signal) {
        int N = signal.length;
        Complex dftCoeff = new Complex();
        double sumReal = 0;
        double sumImg = 0;

        for (int i = 0; i < N; i++) {
            double theta = 2 * Math.PI * k * i / N;
            double cosTheta = Math.cos(theta);
            double sinTheta = Math.sin(theta);
            
            sumReal += signal[i].real * cosTheta + signal[i].img * sinTheta;
            sumImg += -signal[i].real * sinTheta + signal[i].img * cosTheta;
        }
        
        dftCoeff.real = sumReal / N;
        dftCoeff.img = sumImg / N;

        return dftCoeff;
    }

    /**
     * i番目の1次元IDFTの結果を返すメソッド
     */
    public static Complex idft(int i, Complex[] spectrum) {
        int N = spectrum.length;
        Complex idftCoeff = new Complex();
        double sumReal = 0;
        double sumImg = 0;
        
        for (int k = 0; k < N; k++) {
            double theta = 2 * Math.PI * k * i / N;
            double cosTheta = Math.cos(theta);
            double sinTheta = Math.sin(theta);
            
            sumReal += spectrum[k].real * cosTheta - spectrum[k].img * sinTheta;
            sumImg += spectrum[k].real * sinTheta + spectrum[k].img * cosTheta;
        }
        
        idftCoeff.real = sumReal;
        idftCoeff.img = sumImg;

        return idftCoeff;
    }
}