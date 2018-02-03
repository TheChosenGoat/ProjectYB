package CollisionEngineTest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ExtraMethodsDatabase {

	/*
	 * This class should not be constructed.
	 */
	private ExtraMethodsDatabase() {
	}


	/*
	 * A func that reads the high score from the ScoreSheetfile and returns it. Used
	 * only for DodgeTheBall.
	 * 
	 * @return the high score from ScoreSheet.
	 */
	public static int readHighScoreFromFile() {
		BufferedReader br = null;
		int highScore = 0;
		try {
			br = new BufferedReader(new FileReader("src/ScoreSheet"));

			String ScoreSheetLine = br.readLine();
			//If there is nothing written in ScoreSheet high score is -1.
			if(ScoreSheetLine == null) {
				highScore = -1;
			}
			else {


				//Splitting the line of text read from ScoreSheet in every point
				//where there is a "##" inserting split parts into n[].
				String n[] = ScoreSheetLine.split("##");
				//If there is no high score then highScore = -1.
				if(n[1].equals(""))
					highScore = -1;
				//Putting the high-score in the second cell of the array.
				else
					highScore = Integer.parseInt(n[1]);
			}

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {
				//closing br.
				if (br != null)
					br.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}

		}
		return highScore;



	}
	/*
	 * A func that updated the high score in the ScoreSheet file.
	 * 
	 * @param highScore- the new high score.
	 */
	public static void writeHighScoreToFile(int highScore) {
		BufferedReader br = null;
		BufferedWriter bw = null;
		try {
			br = new BufferedReader(new FileReader("src/ScoreSheet"));
			String ScoreSheetLine = br.readLine();
			//Splitting the line of text read from ScoreSheet in every point
			//where there is a "##" and inserting split parts into n[].
			if(ScoreSheetLine != null) {
				String n[] = ScoreSheetLine.split("##");
				//putting the String value of highScore into the second cell.
				n[1] = String.valueOf(highScore);
				//Combining the updated line into a StringBuilder.
				StringBuilder builder = new StringBuilder();
				for(String part : n) {
					builder.append(part);
					builder.append("##");
				}
				//Inserting new String the builder's text without the last 2 characters
				//(they are "##".
				String newScoreSheetLine = builder.substring(0, builder.length()-2);
				//The FileWriter built for bw resets ScoreSheet.
				bw = new BufferedWriter(new FileWriter("src/ScoreSheet"));
				//Writing the new ScoreSheet line into ScoreSheet
				bw.write(newScoreSheetLine);
			}
		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {
				//Closing br and bw.
				if (br != null)
					br.close();

				if (bw != null) {
					bw.flush();
					bw.close();
				}
			} catch (IOException ex) {

				ex.printStackTrace();

			}

		}



	}
}
