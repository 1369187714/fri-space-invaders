package engine;

import java.awt.*;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import engine.DrawManager.SpriteType;
import engine.DrawManager;

/**
 * Manages files used in the application.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public final class FileManager {

	/** Singleton instance of the class. */
	private static FileManager instance;
	/** Application logger. */
	private static Logger logger;
	/** Max number of high scores. */
	private static final int MAX_SCORES = 7;

	private static int playerShipShape,playerShipColor;

	private static int uasnums;

	private static int shipShape;

	/**
	 * private constructor.
	 */
	private FileManager() {
		logger = Core.getLogger();
	}

	/**
	 * Returns shared instance of FileManager.
	 *
	 * @return Shared instance of FileManager.
	 */
	protected static FileManager getInstance() {
		if (instance == null)
			instance = new FileManager();
		return instance;
	}

	/**
	 * Loads sprites from disk.
	 *
	 * @param spriteMap
	 *            Mapping of sprite type and empty boolean matrix that will
	 *            contain the image.
	 * @throws IOException
	 *             In case of loading problems.
	 */
	public void loadSprite(final Map<SpriteType, boolean[][]> spriteMap)
			throws IOException {
		InputStream inputStream = null;

		try {
			String graphicsName;
			if(playerShipShape == 0){
				graphicsName = "graphics";
			}else if(playerShipShape == 1){
				graphicsName = "graphics_1";
			}else
				graphicsName = "graphics_2";
			inputStream = DrawManager.class.getClassLoader()
					.getResourceAsStream(graphicsName);
			char c;

			// Sprite loading.
			for (Map.Entry<SpriteType, boolean[][]> sprite : spriteMap
					.entrySet()) {
				for (int i = 0; i < sprite.getValue().length; i++)
					for (int j = 0; j < sprite.getValue()[i].length; j++) {
						do
							c = (char) inputStream.read();
						while (c != '0' && c != '1');

						if (c == '1')
							sprite.getValue()[i][j] = true;
						else
							sprite.getValue()[i][j] = false;
					}
				logger.fine("Sprite " + sprite.getKey() + " loaded.");
			}
			if (inputStream != null)
				inputStream.close();
		} finally {
			if (inputStream != null)
				inputStream.close();
		}
	}

	public void loadSprite_Temp(final Map<SpriteType, boolean[][]> spriteMap)
			throws IOException {
		InputStream inputStream = null;
		PermanentState permanentState = PermanentState.getInstance();
		shipShape = permanentState.getShipShape();

		try {
			String graphicsName;
			if(shipShape == 0){
				graphicsName = "graphics";
			}else if(shipShape == 1){
				graphicsName = "graphics_1";
			}else
				graphicsName = "graphics_2";
			inputStream = DrawManager.class.getClassLoader()
					.getResourceAsStream(graphicsName);
			char c;

			// Sprite loading.
			for (Map.Entry<SpriteType, boolean[][]> sprite : spriteMap
					.entrySet()) {
				for (int i = 0; i < sprite.getValue().length; i++)
					for (int j = 0; j < sprite.getValue()[i].length; j++) {
						do
							c = (char) inputStream.read();
						while (c != '0' && c != '1');

						if (c == '1')
							sprite.getValue()[i][j] = true;
						else
							sprite.getValue()[i][j] = false;
					}
				logger.fine("Sprite " + sprite.getKey() + " loaded.");
			}
			if (inputStream != null)
				inputStream.close();
		} finally {
			if (inputStream != null)
				inputStream.close();
		}
	}

	/**
	 * Loads a font of a given size.
	 *
	 * @param size
	 *            Point size of the font.
	 * @return New font.
	 * @throws IOException
	 *             In case of loading problems.
	 * @throws FontFormatException
	 *             In case of incorrect font format.
	 */
	public Font loadFont(final float size) throws IOException,
			FontFormatException {
		InputStream inputStream = null;
		Font font;

		try {
			// Font loading.
			inputStream = FileManager.class.getClassLoader()
					.getResourceAsStream("font.ttf");
			font = Font.createFont(Font.TRUETYPE_FONT, inputStream).deriveFont(
					size);
		} finally {
			if (inputStream != null)
				inputStream.close();
		}

		return font;
	}

	/**
	 * Returns the application default scores if there is no user high scores
	 * file.
	 *
	 * @return Default high scores.
	 * @throws IOException
	 *             In case of loading problems.
	 */
	private List<Score> loadDefaultHighScores() throws IOException {
		List<Score> highScores = new ArrayList<Score>();
		InputStream inputStream = null;
		BufferedReader reader = null;

		try {
			inputStream = FileManager.class.getClassLoader()
					.getResourceAsStream("scores");
			reader = new BufferedReader(new InputStreamReader(inputStream));

			Score highScore = null;
			String name = reader.readLine();
			String score = reader.readLine();
			String per = reader.readLine();

			while ((name != null) && (score != null) && (per != null)) {

				highScore = new Score(name, Integer.parseInt(score), Float.parseFloat(per));
				highScores.add(highScore);
				name = reader.readLine();
				score = reader.readLine();
				per = reader.readLine();
			}
		} finally {
			if (inputStream != null)
				inputStream.close();
		}

		return highScores;
	}

	/**
	 * Loads high scores from file, and returns a sorted list of pairs score -
	 * value.
	 *
	 * @return Sorted list of scores - players.
	 * @throws IOException
	 *             In case of loading problems.
	 */
	public List<Score> loadHighScores() throws IOException {

		List<Score> highScores = new ArrayList<Score>();
		InputStream inputStream = null;
		BufferedReader bufferedReader = null;

		try {
			String jarPath = FileManager.class.getProtectionDomain()
					.getCodeSource().getLocation().getPath();
			jarPath = URLDecoder.decode(jarPath, "UTF-8");

			String scoresPath = new File(jarPath).getParent();
			scoresPath += File.separator;
			scoresPath += "scores";

			File scoresFile = new File(scoresPath);
			inputStream = new FileInputStream(scoresFile);
			bufferedReader = new BufferedReader(new InputStreamReader(
					inputStream, Charset.forName("UTF-8")));

			logger.info("Loading user high scores.");

			Score highScore = null;
			String name = bufferedReader.readLine();
			String score = bufferedReader.readLine();
			String per = bufferedReader.readLine();

			while ((name != null) && (score != null) && (per != null)) {

				highScore = new Score(name, Integer.parseInt(score), Float.parseFloat(per));
				highScores.add(highScore);
				name = bufferedReader.readLine();
				score = bufferedReader.readLine();
				per = bufferedReader.readLine();
			}
		} catch (NumberFormatException e) {
			// loads default if there's no latest scores file
			logger.info("Loading default high scores");
			highScores = loadDefaultHighScores();
		} catch (FileNotFoundException e) {
			// loads default if there's no user scores.
			logger.info("Loading default high scores.");
			highScores = loadDefaultHighScores();
		} finally {
			if (bufferedReader != null)
				bufferedReader.close();
		}

		Collections.sort(highScores);
		return highScores;
	}

	/**
	 * Saves user high scores to disk.
	 *
	 * @param highScores
	 *            High scores to save.
	 * @throws IOException
	 *             In case of loading problems.
	 */
	public void saveHighScores(final List<Score> highScores)
			throws IOException {
		OutputStream outputStream = null;
		BufferedWriter bufferedWriter = null;

		try {
			String jarPath = FileManager.class.getProtectionDomain()
					.getCodeSource().getLocation().getPath();
			jarPath = URLDecoder.decode(jarPath, "UTF-8");

			String scoresPath = new File(jarPath).getParent();
			scoresPath += File.separator;
			scoresPath += "scores";

			File scoresFile = new File(scoresPath);

			if (!scoresFile.exists())
				scoresFile.createNewFile();

			outputStream = new FileOutputStream(scoresFile);
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(
					outputStream, Charset.forName("UTF-8")));

			logger.info("Saving user high scores.");

			// Saves 7 or less scores.
			int savedCount = 0;
			for (Score score : highScores) {
				if (savedCount >= MAX_SCORES)
					break;
				if(score.getScore() < 0 ){continue;}

				bufferedWriter.write(score.getName());
				bufferedWriter.newLine();
				bufferedWriter.write(Integer.toString(score.getScore()));
				bufferedWriter.newLine();
				bufferedWriter.write(Float.toString(score.getPer()));
				bufferedWriter.newLine();
				savedCount++;
			}

		} finally {
			if (bufferedWriter != null)
				bufferedWriter.close();
		}
	}

	private int loadDefaultCoins() throws IOException {
		int savedCoins = 0;
		InputStream inputStream = null;
		BufferedReader reader = null;
		try {
			inputStream = FileManager.class.getClassLoader()
					.getResourceAsStream("coins");
			reader = new BufferedReader(new InputStreamReader(inputStream));

			savedCoins = Integer.parseInt(reader.readLine());
		} finally {
			if (inputStream != null)
				inputStream.close();
		}

		return savedCoins;
	}

	public int loadCoins() throws IOException {
		int savedCoins = 0;
		InputStream inputStream = null;
		BufferedReader bufferedReader = null;

		try {
			String jarPath = FileManager.class.getProtectionDomain()
					.getCodeSource().getLocation().getPath();
			jarPath = URLDecoder.decode(jarPath, "UTF-8");

			String coinsPath = new File(jarPath).getParent();
			coinsPath += File.separator;
			coinsPath += "coins";

			File coinsFile = new File(coinsPath);
			inputStream = new FileInputStream(coinsFile);
			bufferedReader = new BufferedReader(new InputStreamReader(
					inputStream, Charset.forName("UTF-8")));

			logger.info("Loading user coins");

			savedCoins = Integer.parseInt(bufferedReader.readLine());
		} catch (FileNotFoundException e) {
			logger.info("Loading default coins.");
			savedCoins = loadDefaultCoins();
		} finally {
			if (bufferedReader != null)
				bufferedReader.close();
		}

		return savedCoins;
	}

	public void saveCoins(final int coins) throws IOException {
		OutputStream outputStream = null;
		BufferedWriter bufferedWriter = null;

		try	{
			String jarPath = FileManager.class.getProtectionDomain()
					.getCodeSource().getLocation().getPath();
			jarPath = URLDecoder.decode(jarPath, "UTF-8");

			String coinsPath = new File(jarPath).getParent();
			coinsPath += File.separator;
			coinsPath += "coins";

			File coinsFile = new File(coinsPath);

			if (!coinsFile.exists())
				coinsFile.createNewFile();

			outputStream = new FileOutputStream(coinsFile);
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(
					outputStream, Charset.forName("UTF-8")));

			logger.info("Saving coins");

			bufferedWriter.write(String.valueOf(coins));
		} finally {
			if (bufferedWriter != null)
				bufferedWriter.close();
		}
	}

	public void Savefile(GameState gamestate) {
		try {
			String jarPath = FileManager.class.getProtectionDomain()
					.getCodeSource().getLocation().getPath();
			jarPath = URLDecoder.decode(jarPath, "UTF-8");
			File file = new File(jarPath + "../save");
			BufferedWriter save = new BufferedWriter(new FileWriter(file));

					String state = Integer.toString(gamestate.getLevel() + 1) + ' ' +
					Integer.toString(gamestate.getScore()) + ' ' +
					Integer.toString(gamestate.getLivesRemaining()) + ' ' +
					Integer.toString(gamestate.getBulletsShot()) + ' ' +
					Integer.toString(gamestate.getShipsDestroyed());

			save.write(state);

			save.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String[] loadInfo(){
		String[] array = {"1","0","3","0","0"};
		try {
			String jarPath = FileManager.class.getProtectionDomain()
					.getCodeSource().getLocation().getPath();
			jarPath = URLDecoder.decode(jarPath, "UTF-8");
			String savePath = new File(jarPath).getParent();
			savePath += File.separator;
			savePath += "save";
			File saveFile = new File(savePath);
			BufferedReader br = new BufferedReader(new FileReader(saveFile));
			String save_info = br.readLine();
			array = save_info.split(" ");
			logger.info("Finish loading.");
		}
		catch (FileNotFoundException e) {
			logger.info("Save file is not found.");
			logger.info("Starting New Game.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			return array;
		}
	}

	public void readship()
			throws IOException {

		InputStream inputStream = null;
		try {
			inputStream = DrawManager.class.getClassLoader().getResourceAsStream("ship");
			playerShipShape = inputStream.read() - 48 - 1;
			playerShipColor = inputStream.read() - 48 - 1;
			uasnums = inputStream.read() - 48 - 1;
			logger.fine("ship read.");
			if (inputStream != null)
				inputStream.close();
		} finally {
			if (inputStream != null)
				inputStream.close();
		}
	}

	public static int getPlayerShipShape() {
		return playerShipShape;
	}
	public static void setPlayerShipShape() throws IOException {
		FileReader fileReader = new FileReader("res/ship");
		fileReader.read();
		int ColorNum = fileReader.read();
		fileReader.close();
		FileWriter fileWriter = new FileWriter("res/ship");
		PrintWriter printWriter = new PrintWriter(fileWriter);
		printWriter.print(shipShape+1);
		playerShipShape = shipShape;
		printWriter.print(ColorNum - 48);
		fileWriter.close();
	}

	public static int getUasnums(){
		return uasnums;
	}
	public static void setUaShipNum(boolean isbuy) {
		try {
			FileReader fileReader = new FileReader("res/ship");
			int shapeNum = fileReader.read();
			int shipColor = fileReader.read();
			int UaShipNum = fileReader.read();
			fileReader.close();
			FileWriter fileWriter = new FileWriter("res/ship");
			PrintWriter printWriter = new PrintWriter(fileWriter);
			printWriter.print(shapeNum - 48);
			printWriter.print(shipColor - 48);
			if(isbuy) {
				printWriter.print(UaShipNum - 48 + 1);
				uasnums = UaShipNum - 48 + 1 - 1;
			}else {
				printWriter.print(UaShipNum - 48 - 1);
				uasnums = UaShipNum - 48 - 1 - 1;
			}
			fileWriter.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static int getPlayerShipColor() {
		return playerShipColor;
	}
	public static void setPlayerShipColor(int shipColor) {
		try {
			FileReader fileReader = new FileReader("res/ship");
			int shapeNum = fileReader.read();
			fileReader.close();
			FileWriter fileWriter = new FileWriter("res/ship");
			PrintWriter printWriter = new PrintWriter(fileWriter);
			printWriter.print(shapeNum - 48);
			printWriter.print(shipColor+1);
			playerShipColor = shipColor;
			fileWriter.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	public static Color ChangeIntToColor(){
		if(playerShipColor == 1) return Color.blue;
		else if(playerShipColor == 2) return Color.darkGray;
		else return Color.GREEN;
	}
}

