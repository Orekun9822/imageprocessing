public class Complex {
	
	public double real;
	public double img;
	
	//コンストラクタ1：0で実部も虚部も初期化
	Complex(){	
		real = 0;
		img = 0;
	}
	//コンストラクタ2：引数の値で実部と虚部の値を初期化
	Complex(double r, double i){
		real = r;
		img  = i;
	}
}