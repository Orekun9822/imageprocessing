import java.util.Random;

public class Kadai10{
	
     public static void main(String args[]){
		//10個の値で構成されるランダム信号の生成
		Complex[] signal1 = new Complex[10];
		Random rdm = new Random();
		for(int i = 0; i < 10; i++){
			Complex objComp = new Complex(10*rdm.nextDouble(),10*rdm.nextDouble());	//複素信号の生成
			//Complex objComp = new Complex(10*rdm.nextDouble(),0);	//実数信号成分の生成
			signal1[i] = objComp;				//複素信号の作成：先頭から順に成分を追加。
		}

     	System.out.println("************* 信号の表示 *************");
		//複素信号の表示。信号の先頭から順に表示している。
		for(int j=0; j < signal1.length ; j++){
			System.out.println("f[" + j + "]=" + signal1[j].real + "+ j" +signal1[j].img );
		}
     	

     	//DFTの実行と結果表示
     	System.out.println("************* DFT後の周波数成分の表示 *************");
     	//周波数スペクトル
     	Complex[] spectrum = new Complex[10];
     	
     	// kの値を変えながらCkを計算する。（spectrum[k] == Ck）
     	for(int k=0; k < spectrum.length ; k++){
     		spectrum[k] = dft(k,signal1);
			System.out.println("F[" + k + "]=" + spectrum[k].real + "+ j" + spectrum[k].img );
		}
     	
		Complex[] spectrum2 = new Complex[10];
     	//IDFTの実行と結果表示
     	System.out.println("************* IDFT後の信号の表示 *************");
		for(int k=0; k < spectrum.length ; k++){
			spectrum2[k] = idft(k,spectrum);
			System.out.println("f[" + k + "]=" + spectrum2[k].real + "+ j" + spectrum2[k].img );
     }
	}

 	
	/************************************************************************
	 **                                                                    **
	 ** k番目の1次元DFTの結果を返すメソッド                                **
	 ** int k：基本周波数の定数倍を示す係数（k=0～信号長-1）               **
	 ** Comp[] signal：信号配列（実部＋虚部）                              **
	 **                                                                    **
	 ************************************************************************/
	public static Complex dft(int k, Complex[] signal){
		
		Complex dftCoeff = new Complex();		//DFTの計算結果を格納するオブジェクト
		
		
		// ***引数kの値のときのCkを計算する。
		// Σ部分の計算は、iの値を変えながらN-1（信号の長さ）まで加算する。
			int N = signal.length; 		// 信号の長さを取得
			dftCoeff.real = 0;
			dftCoeff.img = 0;
			for(int i=0;i<N;i++){
				dftCoeff.real += signal[i].real * Math.cos(2 * Math.PI * k * i / N) - signal[i].img * Math.sin(2 * Math.PI * k * i / N);
				dftCoeff.img += signal[i].real * Math.sin(2 * Math.PI * k * i / N) + signal[i].img * Math.cos(2 * Math.PI * k * i / N);
			}
			dftCoeff.real /= N;		//平均化
			dftCoeff.img /= N;		//平均化
		
		return dftCoeff;
	}
	
	
	public static Complex idft(int k, Complex[] signal){
		Complex idftCoeff = new Complex();		//IDFTの計算結果を格納するオブジェクト
		
		// ***引数kの値のときのCkを計算する。
		// Σ部分の計算は、iの値を変えながらN-1（信号の長さ）まで加算する。
			int N = signal.length; 		// 信号の長さを取得
			idftCoeff.real = 0;
			idftCoeff.img = 0;
			for(int i=0;i<N;i++){
				//オイラーの公式とパラメータkとiに基づき三角関数を計算
				idftCoeff.real += signal[i].real * Math.cos(2 * Math.PI * k * i / N) + signal[i].img * Math.sin(2 * Math.PI * k * i / N);
				idftCoeff.img += signal[i].real * Math.sin(2 * Math.PI * k * i / N) - signal[i].img * Math.cos(2 * Math.PI * k * i / N);
			}
		
		return idftCoeff;
	}
}
