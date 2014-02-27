package de.yunx.datamerge.measures.similarity;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Invoking this classes method getSimilarity(String s1, String s2) compares the
 * similarity of two strings by tokenizing it, matching a class to every token
 * and comparing.
 * 
 * @author Benjamin Brand
 */
public class StandardsSim3 extends Similarity {

	static Level loglevel = Level.INFO;
	static Logger log = Logger.getLogger(StandardsSim3.class.getName());
	static ConsoleHandler consolehandler = new ConsoleHandler();

	private Levenshtein lev;

	public StandardsSim3() {

		log.setUseParentHandlers(false);
		consolehandler.setLevel(loglevel);
		log.addHandler(consolehandler);
		log.setLevel(loglevel);

		lev = new Levenshtein();

		pattern1 = Pattern.compile("[^a-zA-Z0-9 ]*");
		pattern2 = Pattern.compile("[a-zA-Z]*");
		pattern3 = Pattern
				.compile("[a-zA-Z]{0,1}[0-9]{1,2}[,\\.][0-9][,\\.][0-9]{1,2}");
		pattern4 = Pattern.compile("\\([0-9\\.,]+\\)");
		pattern5 = Pattern.compile("[0-9]{2,3}\\.[0-9]{2,3}");
		pattern6 = Pattern.compile("[0-9\\-]{2,}");
	}

	/**
	 * Compares the similarity of two strings by tokenizing it, matching a class
	 * to every token and comparing.
	 * 
	 * @param string
	 *            to compare
	 * @return the likelihood of the string s matching. >0.5 can be considered
	 *         matching.
	 */
	@Override
	public double getSimilarity(String s1, String s2) {
		int[] scores = getSimilarityArray(s1, s2);
		return scores[0] * 0.1 + scores[1] * 0.3 + scores[2] * 0.5 + scores[3]
				* 0.1;
	}

	/**
	 * Returns Array (5dim) containing 0 or 1 depending on whether the following
	 * match:
	 * 
	 * final static int OTHER = 0; final static int AGENCY = 1; final static int
	 * NORM = 2; final static int ID = 3; final static int VERSION = 4;
	 * 
	 * @param s1
	 * @param s2
	 * @return 0 or 1
	 */
	public int[] getSimilarityArray(String s1, String s2) {

		Entity[] al1 = classify(s1);
		Entity[] al2 = classify(s2);

		// 0=AGENCY, 1=NORM,.. siehe inner class entity
		// 5,6,7 = subversions
		int[] sumweights = new int[7];
		for (int i = 0; i < sumweights.length; i++)
			sumweights[i] = 0;

		for (int i = 0; i < al1.length; i++) {

			for (int j = 0; j < al2.length; j++) {
				log.finest(al1[i].str + " (" + al1[i].getClassification()
						+ ")  >  " + al2[j].str + " ("
						+ al2[j].getClassification() + ")");
				if (al1[i].getClassification() == al2[j].getClassification()) {

					if (al2[j].getClassification() == Entity.AGENCY) { // Angency
						// System.out.println("agency");
						if (al1[i].str.equals(al2[j].str)) {
							sumweights[Entity.AGENCY - 1] = 1;
						}
					} else if (al2[j].getClassification() == Entity.NORM) {
						// System.out.println("norm");
						if (al1[i].str.equals(al2[j].str)) {
							sumweights[Entity.NORM - 1] = 1;
						}
					} else if (al2[j].getClassification() == Entity.ID) {
						// System.out.println("id");
						int minlength = Math.min(al1[i].str.length(),
								al2[j].str.length()); // sometimes trailed by
														// ...-4
														// rtc. -> cut off
						if (minlength < 5) {
							if (al1[i].str.equals(al2[j].str)) {
								sumweights[Entity.ID - 1] = 1;
							}
						} else {
							if (al1[i].str.substring(0, minlength).equals(
									al2[j].str.substring(0, minlength))) {
								sumweights[Entity.ID - 1] = 1;
							}
						}
					} else if (al2[j].getClassification() == Entity.VERSION) {
						// System.out.println("version");
						if (al1[i].subversions[0]!=-1 && al1[i].subversions[0] == al2[j].subversions[0])
							sumweights[4] = 1;
						if (al1[i].subversions[1]!=-1 && al1[i].subversions[1] == al2[j].subversions[1])
							sumweights[5] = 1;
						if (al1[i].subversions[2]!=-1 && al1[i].subversions[2] == al2[j].subversions[2])
							sumweights[6] = 1;
						if (lev.getSimilarity(al1[i].str, al2[j].str) < 2) {
							sumweights[Entity.VERSION - 1] = 1;
						}
					}
				}

			}
		}

		return sumweights;

	}

	/**
	 * A string is split into single tokens and classified final static int
	 * OTHER = 0; final static int AGENCY = 1; final static int NORM = 2; final
	 * static int ID = 3; final static int VERSION = 4;
	 * 
	 * @param s1
	 *            String to be split
	 * @return ArrayList Containing instances of class Entity
	 */

	Pattern pattern1;
	Pattern pattern2;
	Pattern pattern3;
	Pattern pattern4;
	Pattern pattern5;
	Pattern pattern6;

	private Entity[] classify(String s1) {
		Entity[] out = {};
		Entity[] recursive = {};
		Matcher matcher;
		Matcher matcher2;
		s1 = s1.replaceAll("([0-9]) ([0-9])", "$1$2"); // TODO nicht elegant
		String[] s1a;
		if (s1.contains("*")) {
			s1a = s1.substring(0, s1.indexOf("*")).split(" ");
			recursive = classify(s1.substring(s1.indexOf("*") + 1, s1.length()));
		} else {
			s1a = s1.split(" ");
		}
		out = new Entity[s1a.length];
		for (int i = 0; i < s1a.length; i++) {
			Entity ent = new Entity(s1a[i]);

			matcher = pattern1.matcher(s1a[i]);
			// if (s1a[i].matches("[^a-zA-Z0-9 ]*")) // special chars
			// ;_-./&"%§&=
			if (matcher.matches()) // ...
				ent.incWeight(Entity.OTHER, 10);
			// if (s1a[i].matches("[a-zA-Z]*")) {
			matcher = pattern2.matcher(s1a[i]);
			if (matcher.matches()) {
				if (i == 0) {
					if (s1a.length > i + 1) {
						matcher2 = pattern2.matcher(s1a[i + 1]);
						// if (s1a[i + 1].matches("[a-zA-Z]*"))
						if (matcher2.matches())
							ent.incWeight(Entity.AGENCY, 8);
					} else
						ent.incWeight(Entity.AGENCY, 3);
					ent.incWeight(Entity.NORM, 6);
				}
				if (ent.str.toLowerCase().equals("v"))
					ent.incWeight(Entity.OTHER, 6);
				if (ent.str.toLowerCase().equals("version"))
					ent.incWeight(Entity.OTHER, 16);
				else if (i == 1)
					ent.incWeight(Entity.NORM, 8);
				else
					ent.incWeight(Entity.NORM, 1);
			}
			matcher = pattern3.matcher(s1a[i]);
			matcher2 = pattern4.matcher(s1a[i]);
			if (matcher.matches() || matcher2.matches()) {
				// if (s1a[i]
				// .matches("[a-zA-Z]{0,1}[0-9]{1,2}[,\\.][0-9][,\\.][0-9]{1,2}")
				// || s1a[i].matches("\\([0-9\\.,]+\\)")) {

				ent.incWeight(Entity.VERSION, 5 + i);
				if (i > 0)
					if (s1a[i - 1].startsWith("v")
							|| s1a[i - 1].startsWith("V")) // Version
						ent.incWeight(Entity.VERSION, 2);
				if (i > 0)
					if (s1a[i - 1].startsWith("version")
							|| s1a[i - 1].startsWith("Version")) // Version
						ent.incWeight(Entity.VERSION, 9);

			}
			matcher = pattern5.matcher(s1a[i]);
			if (matcher.matches()) {
				// if (s1a[i].matches("[0-9]{2,3}\\.[0-9]{2,3}")) {
				ent.incWeight(Entity.ID, 7);
			}
			matcher = pattern6.matcher(s1a[i]);
			if (matcher.matches()) {
				ent.incWeight(Entity.ID, 8);
				// if (i + 1 < s1a.length && s1a[i + 1].matches("[0-9\\-]{2,}"))
				// {

				if (i + 1 < s1a.length) {
					matcher = pattern6.matcher(s1a[i + 1]);
					if (matcher.matches())
						ent.incWeight(Entity.ID, 2);
				}

			}
			out[i] = ent;

			log.finer(ent.str + "  ->>   " + ent.getClassification());
		}

		// if several standards per line (divided by *)
		if (recursive.length > 0) {
			Entity[] out_concat = new Entity[out.length + recursive.length];
			for (int j = 0; j < out.length; j++)
				out_concat[j] = out[j];
			for (int j = 0; j < recursive.length; j++)
				out_concat[j + out.length] = recursive[j];
			return out_concat;
		} else
			return out;
	}

	// private Entity[] classify(String s1) {
	// Entity[] out = {};
	// Entity[] recursive = {};
	// Matcher matcher1 = fPattern.matcher("[^a-zA-Z0-9 ]*");
	// s1 = s1.replaceAll("([0-9]) ([0-9])", "$1$2");
	// String[] s1a;
	// if (s1.contains("*")) {
	// s1a = s1.substring(0, s1.indexOf("*")).split(" ");
	// recursive = classify(s1.substring(s1.indexOf("*") + 1, s1.length()));
	// } else {
	// s1a = s1.split(" ");
	// }
	// out = new Entity[s1a.length];
	// for (int i = 0; i < s1a.length; i++) {
	// Entity ent = new Entity(s1a[i]);
	// if (s1a[i].matches("[^a-zA-Z0-9 ]*")) // special chars ;_-./&"%§&=
	// // ...
	// ent.incWeight(Entity.OTHER, 10);
	// if (s1a[i].matches("[a-zA-Z]*")) {
	// if (i == 0) {
	// if (s1a.length > i + 1 && s1a[i + 1].matches("[a-zA-Z]*"))
	// ent.incWeight(Entity.AGENCY, 8);
	// else
	// ent.incWeight(Entity.AGENCY, 3);
	// ent.incWeight(Entity.NORM, 6);
	// }
	// if (ent.str.toLowerCase().equals("v"))
	// ent.incWeight(Entity.OTHER, 6);
	// if (ent.str.toLowerCase().equals("version"))
	// ent.incWeight(Entity.OTHER, 16);
	// else if (i == 1)
	// ent.incWeight(Entity.NORM, 8);
	// else
	// ent.incWeight(Entity.NORM, 1);
	// }
	// if (s1a[i]
	// .matches("[a-zA-Z]{0,1}[0-9]{1,2}[,\\.][0-9][,\\.][0-9]{1,2}")
	// || s1a[i].matches("\\([0-9\\.,]+\\)")) {
	// // System.out.println("------------"+s1a[i]);
	// ent.incWeight(Entity.VERSION, 5 + i);
	// if (i > 0)
	// if (s1a[i - 1].startsWith("v")
	// || s1a[i - 1].startsWith("V")) // Version
	// ent.incWeight(Entity.VERSION, 2);
	// if (i > 0)
	// if (s1a[i - 1].startsWith("version")
	// || s1a[i - 1].startsWith("Version")) // Version
	// ent.incWeight(Entity.VERSION, 9);
	//
	// }
	// if (s1a[i].matches("[0-9]{2,3}\\.[0-9]{2,3}")) {
	// ent.incWeight(Entity.ID, 7);
	// }
	// if (s1a[i].matches("[0-9\\-]{2,}")) {
	// ent.incWeight(Entity.ID, 8);
	// if (i + 1 < s1a.length && s1a[i + 1].matches("[0-9\\-]{2,}")) {
	// ent.incWeight(Entity.ID, 2);
	//
	// }
	//
	// }
	// out[i] = ent;
	//
	// log.finer(ent.str + "  ->>   " +
	// ent.getClassification());
	// }
	//
	// // if several standards per line (divided by *)
	// if (recursive.length > 0) {
	// Entity[] out_concat = new Entity[out.length + recursive.length];
	// for (int j = 0; j < out.length; j++)
	// out_concat[j] = out[j];
	// for (int j = 0; j < recursive.length; j++)
	// out_concat[j + out.length] = recursive[j];
	// return out_concat;
	// } else
	// return out;
	// }sss

	/**
	 * Holds information about a string: classification and a weight for each
	 * (Probability of every classification)
	 * 
	 * @author BB
	 * 
	 */
	class Entity {
		public Entity(String str) {
			this.str = str;
			for (int i = 0; i < subversions.length; i++)
				subversions[i] = -1;
		}

		final static int OTHER = 0;
		final static int AGENCY = 1;
		final static int NORM = 2;
		final static int ID = 3;
		final static int VERSION = 4;
		public int[] subversions = new int[3];
		private int weights[] = new int[5];
		public String str = "";
		private int classe = 0;
		private boolean uptodate = false;

		public void incWeight(int classi, int weight) {
			if (classi <= weights.length) {
				weights[classi] += weight;
				uptodate = false;
			}
		}

		public int getClassification() {
			if (!uptodate) {
				double max = 0;
				for (int i = 0; i < weights.length; i++)
					if (weights[i] > max) {
						max = weights[i];
						classe = i;
					}

				// if version retrieve subversions (2.3.4 -> 2 and 3 and 4)
				if (classe == 4) {
					String s[] = str.replaceAll("[^0-9\\.,]", "").split(
							"[\\.,]");
					for (int i = 0; i < Math.min(s.length, 3); i++)
						// at most 3 numbers / subversions
						try{
						subversions[i] = Integer.parseInt(s[i]);
						}catch(NumberFormatException e){
							log.throwing(Entity.class.getName(), "getClassification", e);
							log.severe("Error trying to parse '"+s[i]+"' from token '"+str+"'.");
						}
					s = null;
				}

				uptodate = true;
			}
			return classe;
		}

	}

}
