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

//�}�`�`��ɕK�v�ȃN���X
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Kadai02 extends Application {

	public static void main(String[] args){
        // ���C�����\�b�h������s
        Application.launch(args);
 
    }
	
	//���摜�̔Z�x�l�z��
    @Override
    public void start(Stage stage) throws Exception {
		Image img = new Image( "kousha.jpg" );		//�摜�t�@�C�����[�h
    	int width = (int) img.getWidth();
        int height= (int) img.getHeight();
    	
    	
		//�P�x�l�݂̂̔z����擾
		int aryKido[][] = getLuminanceArray(img, width, height);			//���摜�̋P�x�l�z����擾
    	
    	WritableImage gImg1 = drawGrayImage(aryKido, width, height);		//���摜���O���C�X�P�[���ɕϊ������`��p�C���X�^���X�̎擾
		
    	//�摜�����̓K�p
    	int aryProcKido[][] = imageProcessing(aryKido, width, height);		//�摜������̋P�x�l�z����擾
    	
    	WritableImage gImg2 = drawGrayImage(aryProcKido, width, height);	//�摜�����K�p��̕`��p�C���X�^���X�̎擾
    	
		ImageView imgView1 = new ImageView( gImg1 );	//ImageVew����
		ImageView imgView2 = new ImageView( gImg2 );	//ImageVew����
		GridPane grid = new GridPane();					//SceneGraph�̃C���X�^���X�����iGridPane�j
    	//grid.setGridLinesVisible(true);				//�f�o�b�O�p�ɃZ���̐���\��
    	grid.setAlignment(Pos.CENTER);					//�t�H�[�������ɔz�u
    	grid.setHgap(10);								//�e�Z���̍��E�Ԃ̃M���b�v��10�s�N�Z���ɐݒ�
		grid.setVgap(10);								//�e�Z���̏㉺�Ԃ̃M���b�v��10�s�N�Z���ɐݒ�

    	//gridpane�̏c���Z�����͎����I�Ɏw�肵���ʒu����ɍŏ����Ɏ������肳���
    	Text title0 = new Text("画像処理名:ヒストグラム");	//�@ �㕔�^�C�g����(0,0)�ɔz�u�B������ ��,�s �̏��Ɏw��B
    	grid.add(title0,0,0);
    	//�@ (1,0)�Ƀ^�C�g���u���摜�v���A���̉�(2,0)�Ɍ��摜��\��
    	Text title1 = new Text("原画像");
    	grid.add(title1,0,1);
    	grid.add(imgView1,0,2);							
    	//�@ (1,1)�Ƀ^�C�g���u�����摜�v���A���̉�(2,1)�ɏ����摜��\��
    	Text title2 = new Text("処理画像");
    	grid.add(title2,1,1);
        grid.add(imgView2,1,2);
    	
    	
    	//***** ���摜�q�X�g�O�����̕`�揈�� *****
    	int[] gengaHist = getHistArray(aryKido, width, height);	//(1)���̃q�X�g�O�����z��̎擾�B�摜�̔Z�x�l�z����֐��ɓn���A�q�X�g�O�����z����󂯎��悤�ɂ���B
    	Canvas gengaCanvas = drawChart(gengaHist,256,200);		//(2)�q�X�g�O�����z�����ɁA�O���t��`�悵��Canvas���쐬����B2�̈����͕`�悷��O���t�̃T�C�Y�ł���B
    	grid.add(gengaCanvas,0,3);								//(3)GridPane��3�s0��Ɍ��摜��Canvas�C���X�^���X��ǉ�
    	
    	//***** �����摜�q�X�g�O�����̕`�揈�� *****
		int[] shoriHist = getHistArray(aryProcKido, width, height);
		Canvas shoriCanvas = drawChart(shoriHist,256,200);
		grid.add(shoriCanvas,1,3);

    	//���摜�̏������Q�l�Ɏ����Ŏ�������B
    	
    	Scene scene = new Scene(grid, 1000, 650);	//�A
 
    	//CSS�ɂ�錩���ڂ̐ݒ�
    	scene.getStylesheets().add("style.css");			//css�t�@�C���ǂݍ���
    	title1.getStyleClass().add("my-title1");			//class�̐ݒ�
    	title2.setId("my-title2");							//id�̐ݒ�
    	
    	stage.setScene(scene);						//�B
        stage.show();								//�C
    }

	/************************************************************************
	 ************************************************************************
	 **                                                                    **
	 ** �q�X�g�O�����`��̂��߂̃��\�b�h  		                           **
	 ** �q�X�g�O�����z�����Ɏw��T�C�Y�ŕ`�悵��Canvas��Ԃ�             **
	 **                                                                    **
	 ************************************************************************
	 ************************************************************************/
	public Canvas drawChart(int[] hist, double width, double height){


		double max=0;
		for(int j=0;j<256;j++)
		{
			if(max<=(double)hist[j])
			{
				max=(double)hist[j];
			}
		}
		double L=0;

		Canvas canvas = new Canvas(width, height);				//Canvas��width�A����height�Ő���
		GraphicsContext gc  = canvas.getGraphicsContext2D();	//Canvas�֕`�悷�邽�߂̃C���X�^���X�𐶐�
		
		gc.setFill(Color.rgb(97, 150, 242));	//�h��Ԃ��̐F��RGB�Ŏw��
		gc.fillRect(0, 0, width, height);		//Canvas������W(0,0)���畝widht�~����height�̓h��Ԃ��ꂽ�����`��`��

		gc.setStroke(Color.rgb(255, 0, 0));		//���̐F���w��
		for(int i=0;i<256;i++)
		{
			L=(hist[i]/max)*height;
			gc.strokeLine(i, height-1,i, height-L-1);//�`�悷��B
		}
		return canvas;		//�`�悪��������Canvas��Ԃ��B
	}	
	
	/************************************************************************
	 ************************************************************************
	 **                                                                    **
	 ** �q�X�g�O�����p�z��̂��߂̃��\�b�h  		                       **
	 ** �Z�x�l�z�����ɁA1�����̃q�X�g�O�����z���Ԃ�                    **
	 **                                                                    **
	 ************************************************************************
	 ************************************************************************/
	public int[] getHistArray(int[][] aryKido, int width, int height){
		
		
		//�����Ƀq�X�g�O�����Z�x�l��v�f�ԍ��A���̔Z�x�l�̉�f����v�f�̒l�Ƃ���z��hist���쐬����悤�Ɏ�������B
		//����́A�Z�x�l0�̉�f��5�A�Z�x�l1��100�A�Z�x�l2��50�������BaryKido�����Ƃɐ�������B
		int[] hist = new int[256];
		for(int i=0;i<width;i++)
		{
			for(int j=0;j<height;j++)
			{
				hist[aryKido[i][j]]++;//i���c���Aj�������ł��̈ʒu�̔Z�x�l���i�[
			}
		}
		

		
		return hist;
	}
	
	/************************************************************************
	 ************************************************************************
	 **                                                                    **
	 ** �ۑ�ɉ������������L�ڂ��郁�\�b�h                                 **
	 ** �O���[�X�P�[���摜�̔Z�x�l�z��z��cpyKido�����                    **
	 ** �������ʂ̋P�x�l�z��aryProcKido�𐶐����ĕԂ�                      **
	 **                                                                    **
	 ************************************************************************
	 ************************************************************************/
	public int[][] imageProcessing(int[][] aryKido, int width, int height ) {
		
		int aryProcKido[][] = new int[width][height];

		//2�d��for��1��f���������s���B
		for(int y=0; y < height; y++ ){
			for(int x=0; x < width; x++ ){
				
				aryProcKido[x][y] = 255 - aryKido[x][y];
			}
		}
		
		return aryProcKido;
	}
	
	/************************************************************************
	 * 
	 * �J���[�摜����O���[�X�P�[���摜��`�擾���郁�\�b�h
	 * ���߂��P�x�l�͔Z�x�l�z��z��aryKido�Ɋi�[����
	 *
	 ************************************************************************/
	public WritableImage drawGrayImage(int aryKido[][], int width, int height ) {
		WritableImage wImg = new WritableImage(width, height);
		PixelWriter writer = wImg.getPixelWriter();

		for(int y=0; y < height; y++ ){
			for(int x=0; x < width; x++ ){
				int kido = aryKido[x][y];
				
				//���ߓx��255�Œ�i���߂Ȃ��j
				writer.setArgb( x , y, createArgb(255, kido, kido, kido));
			}
		}
		
		return wImg;
	}

	/************************************************************************
	 * 
	 * �J���[�摜�̊e��f�̔Z�x�l����P�x�l�����߂郁�\�b�h�B
	 * �߂�l�͋P�x�l���i�[�����e��f�̔Z�x�l�z��
	 *
	 ************************************************************************/
	public int[][] getLuminanceArray(Image img, int width, int height) {
		//�J���[�摜�e��f�̔Z�x�l��ǂݎ�邽��
		PixelReader reader = img.getPixelReader();
				
		//�O���C�X�P�[���摜�̔Z�x�l�l���i�[����z��
		int kidoGaso[][] = new int[width][height];
		
		//�e��f�̔Z�x�l�̃��l�AR�AG�AB���i�[����ϐ�
		int argb, alpha, red, green, blue;
		
		// 1��f����RGB�̒l����P�x�l�����߂�B
		for(int y=0; y < height; y++){
			for(int x=0; x < width; x++){
				//�_���ς��Ƃ邱�ƂŁA�V�t�g��̉���8bit�����̂܂܎c���A���24bit��0�Ƃ���B
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
	 * a,r,g,b����32bit�̔Z�x�l��\��argb�𐶐�����B
	 *
	 ************************************************************************/
	public int createArgb(int a, int r, int g, int b){
		int argb = (a << 24) | (r << 16 ) | ( g << 8 ) | b;
		
		return argb;
	}
	
}
