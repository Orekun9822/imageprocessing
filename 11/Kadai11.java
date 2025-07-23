import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.geometry.Pos;
import javafx.geometry.Insets;

//描画に必要なクラス
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Kadai11 extends Application {

	public static void main(String[] args){
        // メインメソッドから実行
        Application.launch(args);
 
    }
	
	//原画像の濃度値配列
    @Override
    public void start(Stage stage) throws Exception {
		Image img = new Image( "sample.jpg" );		//画像ファイルロード
    	int width = (int) img.getWidth();
        int height= (int) img.getHeight();
    	
    	
		//輝度値のみの配列を取得
		int aryKido[][] = getLuminanceArray(img, width, height);			//原画像の輝度値配列を取得
    	
    	WritableImage gImg1 = drawGrayImage(aryKido, width, height);		//原画像をグレイスケールに変換した描画用インスタンスの取得
		
    	//画像処理の適用
    	int aryProcKido[][] = imageProcessing(aryKido, width, height);		//画像処理後の輝度値配列を取得
    	
    	WritableImage gImg2 = drawGrayImage(aryProcKido, width, height);	//画像処理適用後の描画用インスタンスの取得
    	
		ImageView imgView1 = new ImageView( gImg1 );	//ImageVew生成
		ImageView imgView2 = new ImageView( gImg2 );	//ImageVew生成
		GridPane grid = new GridPane();					//SceneGraphのインスタンス生成（GridPane）
    	//grid.setGridLinesVisible(true);				//デバッグ用にセルの線を表示
    	grid.setAlignment(Pos.CENTER);					//フォーム中央に配置
    	grid.setHgap(10);								//各セルの左右間のギャップを10ピクセルに設定
		grid.setVgap(10);								//各セルの上下間のギャップを10ピクセルに設定

    	//gridpaneの縦横セル数は自動的に指定した位置を基に最小限に自動決定される
    	Text title0 = new Text("画像処理名:2次元DFT・2次元IDFT");	//① 上部タイトルを(0,0)に配置。引数は 列,行 の順に指定。
    	grid.add(title0,0,0);
    	//① (1,0)にタイトル「原画像」を、その下(2,0)に原画像を表示
    	Text title1 = new Text("原画像");
    	grid.add(title1,0,1);
    	grid.add(imgView1,0,2);							
    	//① (1,1)にタイトル「処理画像」を、その下(2,1)に処理画像を表示
    	Text title2 = new Text("処理画像");
    	grid.add(title2,1,1);
        grid.add(imgView2,1,2);
    	    	
    	//***** 処理画像ヒストグラムの描画処理 *****
    	
    	//原画像の処理を参考に自分で実装する。
    	
    	Scene scene = new Scene(grid, 1000, 650);	//②
 
    	//CSSによる見た目の設定
    	scene.getStylesheets().add("style.css");			//cssファイル読み込み
    	title1.getStyleClass().add("my-title1");			//classの設定
    	title2.setId("my-title2");							//idの設定
    	
    	stage.setScene(scene);						//③
        stage.show();								//④
    }


	/************************************************************************
	 ************************************************************************
	 **                                                                    **
	 ** 課題に応じた処理を記載するメソッド                                 **
	 ** グレースケール画像の濃度値配列配列cpyKidoを基に                    **
	 ** 処理結果の輝度値配列aryProcKidoを生成して返す                      **
	 **                                                                    **
	 ************************************************************************
	 ************************************************************************/
	public int[][] imageProcessing(int[][] aryKido, int width, int height ) {
		
		int aryProcKido[][] = new int[width][height];


		// ***** 1回目（横方向）DFT *****

		//DFTの結果を代入する2次元配列dftConnf1
		Complex dftCoeff1[][] = new Complex[width][height];
		
		// 横方向DFTの結果F^[k][n]を計算する。
		//nの値（画像縦方向の座標の値）を固定して繰り返すためのfor文。
		for(int n=0; n < height; n++){

			//画像データから、nの値に対応する横方向濃度値1次元配列を生成する。
			Complex signal1[] = new Complex[width];
			for(int m=0; m < width; m++){
				Complex objCmp = new Complex();
				objCmp.real = aryKido[m][n];	//原画像の画素の濃度値（実数部）を代入
				objCmp.img  = 0;				//原画像の画素の濃度値（虚数部）は0を代入
				signal1[m] = objCmp;			//Complex型1次元配列に完成したComplex型オブジェクトを代入して横方向の濃度値だけ取り出す。
			}

			//kの値を+1ずつ変えながらF^[k,n]の値（複素数）を計算するためのfor文。
			for(int k=0; k < width; k++){
				
				//DFTの計算は、前の課題で作成した1次元DFT関数を利用する
				dftCoeff1[k][n] = dft(k, signal1);
			}
		}

		// *****以下 「1回目（横方向）DFT」を参考に処理を記載する *****
		// ***** 2回目（縦方向）DFT ***** // DFTの結果を代入する2次元配列dftCoeff2
        Complex dftCoeff2[][] = new Complex[width][height];

        // 縦方向DFTの結果F[k][l]を計算する。
        // kの値（画像横方向の周波数成分の座標の値）を固定して繰り返すためのfor文。
        for (int k = 0; k < width; k++) {

            // 1回目のDFT結果から、kの値に対応する縦方向の周波数成分1次元配列を生成する。
            Complex signal2[] = new Complex[height];
            for (int n = 0; n < height; n++) {
                signal2[n] = dftCoeff1[k][n]; // 1回目のDFT結果の複素数をそのまま代入
            }

            // lの値を+1ずつ変えながらF[k][l]の値（複素数）を計算するためのfor文。
            for (int l = 0; l < height; l++) {
                // DFTの計算は、1次元DFT関数を利用する
                dftCoeff2[k][l] = dft(l, signal2);
            }
        }

        // ***** 1回目（横方向）IDFT ***** // IDFTの結果を代入する2次元配列idftCoeff1
        Complex idftCoeff1[][] = new Complex[width][height];

        // 横方向IDFTの結果f'[m][l]を計算する。
        // lの値（画像縦方向の周波数成分の座標の値）を固定して繰り返すためのfor文。
        for (int l = 0; l < height; l++) {

            // 2回目のDFT結果から、lの値に対応する横方向の周波数成分1次元配列を生成する。
            Complex spectrum1[] = new Complex[width];
            for (int k = 0; k < width; k++) {
                spectrum1[k] = dftCoeff2[k][l]; // 2回目のDFT結果の複素数をそのまま代入
            }

            // mの値を+1ずつ変えながらf'[m][l]の値（複素数）を計算するためのfor文。
            for (int m = 0; m < width; m++) {
                // IDFTの計算は、1次元IDFT関数を利用する
                idftCoeff1[m][l] = idft(m, spectrum1);
            }
        }

        // ***** 2回目（縦方向）IDFT ***** // IDFTの結果を代入する2次元配列idftCoeff2
        Complex idftCoeff2[][] = new Complex[width][height];

        // 縦方向IDFTの結果f[m][n]を計算する。
        // mの値（画像横方向の座標の値）を固定して繰り返すためのfor文。
        for (int m = 0; m < width; m++) {

            // 1回目のIDFT結果から、mの値に対応する縦方向の周波数成分1次元配列を生成する。
            Complex spectrum2[] = new Complex[height];
            for (int l = 0; l < height; l++) {
                spectrum2[l] = idftCoeff1[m][l]; // 1回目のIDFT結果の複素数をそのまま代入
            }

            // nの値を+1ずつ変えながらf[m][n]の値（複素数）を計算するためのfor文。
            for (int n = 0; n < height; n++) {
                // IDFTの計算は、1次元IDFT関数を利用する
                idftCoeff2[m][n] = idft(n, spectrum2);
            }
        }

        // ***** 2回目（縦方向）IDFTの結果の実数部だけを1画素ずつaryProcKidoに代入。（虚数部＝0であるのでreturnしない）
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // IDFTの結果は実数部が元の画像データに対応する
                // 値が0-255の範囲に収まるようにクランプする
                int pixelValue = (int) Math.round(idftCoeff2[x][y].real);
                if (pixelValue < 0) {
                    pixelValue = 0;
                } else if (pixelValue > 255) {
                    pixelValue = 255;
                }
                aryProcKido[x][y] = pixelValue;
            }
        }
        
		// aryProcKidoを2次元DFT～2次元IDFTの結果として返す。
		return aryProcKido;
	}
	
	/************************************************************************
	 * 
	 * カラー画像からグレースケール画像を描画得するメソッド
	 * 求めた輝度値は濃度値配列配列aryKidoに格納する
	 *
	 ************************************************************************/
	public WritableImage drawGrayImage(int aryKido[][], int width, int height ) {
		WritableImage wImg = new WritableImage(width, height);
		PixelWriter writer = wImg.getPixelWriter();

		for(int y=0; y < height; y++ ){
			for(int x=0; x < width; x++ ){
				int kido = aryKido[x][y];
				
				//透過度は255固定（透過なし）
				writer.setArgb( x , y, createArgb(255, kido, kido, kido));
			}
		}
		
		return wImg;
	}

	/************************************************************************
	 * 
	 * カラー画像の各画素の濃度値から輝度値を求めるメソッド。
	 * 戻り値は輝度値を格納した各画素の濃度値配列
	 *
	 ************************************************************************/
	public int[][] getLuminanceArray(Image img, int width, int height) {
		//カラー画像各画素の濃度値を読み取るため
		PixelReader reader = img.getPixelReader();
				
		//グレイスケール画像の濃度値値を格納する配列
		int kidoGaso[][] = new int[width][height];
		
		//各画素の濃度値のα値、R、G、Bを格納する変数
		int argb, alpha, red, green, blue;
		
		// 1画素ずつRGBの値から輝度値を求める。
		for(int y=0; y < height; y++){
			for(int x=0; x < width; x++){
				//論理積をとることで、シフト後の下位8bitをそのまま残し、上位24bitは0とする。
				argb = reader.getArgb( x, y);
				alpha = (argb >> 24) & 0xFF;
				red   = (argb >> 16) & 0xFF;
				green = (argb >>  8) & 0xFF;
				blue  =  argb        & 0xFF;
				kidoGaso[x][y] = (int)(0.299 * red + 0.587 * green + 0.114 * blue);
			}
		}
		
		return kidoGaso;
	}
	
	/************************************************************************
	 *
	 * a,r,g,bから32bitの濃度値を表すargbを生成する。
	 *
	 ************************************************************************/
	public int createArgb(int a, int r, int g, int b){
		int argb = (a << 24) | (r << 16 ) | ( g << 8 ) | b;
		
		return argb;
	}

	/************************************************************************
	 **                                                                    **
	 ** k番目の1次元DFTの結果を返すメソッド                                **
	 ** int k：基本周波数の定数倍を示す係数（k=0～信号長-1）               **
	 ** Complex[] aryCmp：信号配列（実部＋虚部）                           **
	 **                                                                    **
	 ************************************************************************/
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
	
	/************************************************************************
	 **                                                                    **
	 ** k番目の1次元IDFTの結果を返すメソッド                                **
	 ** int k：基本周波数の定数倍を示す係数（k=0～信号長-1）               **
	 ** Complex[] aryCmp：信号配列（実部＋虚部）                           **
	 **                                                                    **
	 ************************************************************************/
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