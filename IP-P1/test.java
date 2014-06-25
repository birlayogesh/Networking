import java.io.File;

public class test {
	static int count = 1;
	String tempPath = null;
	File mainFolder = new File("E:\\RFCDb");

	public static void main(String[] args) {
		test lf = new test();
		lf.getFiles(lf.mainFolder);
	}

	public void getFiles(File f) {
		File files[];
		if (f.isFile()) {
			tempPath = f.getAbsolutePath();

			tempPath.replace("/\\", "/");

				System.out
						.println(count++ + "#" + f.getName() + "#" + tempPath);
		} else {
			files = f.listFiles();
			for (int i = 0; i < files.length; i++) {
				getFiles(files[i]);
			}
		}
	}
}