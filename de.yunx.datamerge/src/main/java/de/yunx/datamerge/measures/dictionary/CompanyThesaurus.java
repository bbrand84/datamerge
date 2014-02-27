package de.yunx.datamerge.measures.dictionary;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.avro.util.Utf8;


public class CompanyThesaurus implements Thesaurus {

	public CompanyThesaurus() {
		// TODO Auto-generated constructor stub
	}
	
	private StringBuffer repl( StringBuffer term, String pattern, String replacement){
		Matcher matcher = Pattern.compile(pattern).matcher( term );
		StringBuffer sb = new StringBuffer();
		while ( matcher.find() )
		  matcher.appendReplacement( sb, replacement );
		matcher.appendTail( sb );
		return sb;
	}
	
	/**
	 * Replaces special chars and converts legal company forms to its abbreviations, e.g. "corporated" to "corp"
	 */
	public String use(Utf8 token) {

		//token = token.toString().toLowerCase();
		StringBuffer sb = new StringBuffer(token.toString().toLowerCase());
		token = null;

//		sb = repl(sb, "!", " ");
//		sb = repl(sb, "\"", " ");
//		sb = repl(sb, "§", " ");
//		sb = repl(sb, "$", " ");
//		sb = repl(sb, "%", " ");
//		sb = repl(sb, "&", " ");
//		sb = repl(sb, "/", " ");
//		sb = repl(sb, "\\(", " ");
//		sb = repl(sb, "\\)", " ");
//		sb = repl(sb, "\"", " ");
//		sb = repl(sb, "\\?", " ");
//		sb = repl(sb, "`", " ");
//		sb = repl(sb, "´", " ");
//		sb = repl(sb, "\\\\", " ");
//		sb = repl(sb, "\\}", " ");
//		sb = repl(sb, "\\]", " ");
//		sb = repl(sb, "\\{", " ");
//		sb = repl(sb, "\\[", " ");
//		sb = repl(sb, "\\^", " ");
//		sb = repl(sb, "°", " ");
//		sb = repl(sb, "\\|", " ");
//		sb = repl(sb, "<", " ");
//		sb = repl(sb, ">", " ");
//		sb = repl(sb, "-", " ");
//		sb = repl(sb, "_", " ");
//		sb = repl(sb, ",", " ");
//		sb = repl(sb, ".", " ");
//		sb = repl(sb, ";", " ");
//		sb = repl(sb, ":", " ");
//		sb = repl(sb, "'", " ");
//		sb = repl(sb, "#", " ");
//		sb = repl(sb, "\\*", " ");
//		sb = repl(sb, "\\+", " ");
//		sb = repl(sb, "~", " ");

	sb = repl(sb, "[\\'\\°\\!\\§\\$\\%\\&\\/\\(\\)\\=\\?\\`\\´\\^\\\\}\\]\\[\\{\\,\\.\\-\\_\\:\\;\\#\\*\\+\\~\\<\\>\\|\\]\"]*", "");

		sb = repl(sb, "  ", " ");
		sb = repl(sb, "  ", " ");
		sb = repl(sb, "  ", " ");

		// sb = replAll("[^0-9a-z ]", "");//non character *+§$%/(
		// etc., ALSO umlaut!!


		sb = repl(sb, "gesellschaft mit beschraenkter haftung", "gmbh");
		sb = repl(sb, "gesellschaft mbh", "gmbh");
		sb = repl(sb, "societe par actions simplifiee", "sas");
		sb = repl(sb, "societe anonyme", "sa");
		sb = repl(sb, "aktiengesellschaft", "ag");
		sb = repl(sb, "european economic interest grouping", "eeig");
		sb = repl(sb, "european cooperative society", "sce");
		sb = repl(sb, "european private company", "spe");
		sb = repl(sb, "societas privata europaea", "spe");
		sb = repl(sb, "europäische gesellschaft", "se");
		sb = repl(sb, "societas europaea", "se");
		sb = repl(sb, "delaware general corporation law", "dgcl");
		sb = repl(sb, "model business corporation act", "mbca");
		sb = repl(sb, "community interest company", "cic");
		sb = repl(sb, "limited liability company", "llc");
		sb = repl(sb, "series limited liability company", "sllc");
		sb = repl(sb, "aktiebolag", "ab");
		sb = repl(sb, "public limited company", "plc");
		sb = repl(sb, "ansvarlig selskap", "ans");
//		sb = repl(sb, "a/s", "as");
		sb = repl(sb, "aksjeselskap", "as");
		sb = repl(sb, "kabushiki gaisha", "k.k.");
//		sb = repl(sb, "k.k.", "kk");
		sb = repl(sb, "naamloze vennootschap", "nv");
//		sb = repl(sb, "n.v.", "nv");
		sb = repl(sb, "osakeyhti.{0,2}", "oy");
		sb = repl(sb, "limited liability partnership", "llp");
		sb = repl(sb, "kommanditgesellschaft", "kg");
		sb = repl(sb, "anonymous society", "sa");
//		sb = repl(sb, "s.a.", "sa");
		sb = repl(sb, "r.{0,2}szv.{0,2}nyt.{0,2}rsas.{0,2}g", "rt");
		sb = repl(sb, "akcine bendrove", "ab");
		sb = repl(sb, "akciov.{0,2} spolo.{0,2}nost", "as");
		sb = repl(sb, "akciov.{0,2} spole.{0,2}nost", "as");
//		sb = repl(sb, "a.s.", "as");
		sb = repl(sb, "aktsionernoye obshchestvo", "ao");
		sb = repl(sb, "aktsionerne tovarystvo", "at");
		sb = repl(sb, "aktsionerno druzhestvo", "???");
		sb = repl(sb, "shoq.{0,2}ri aksionare", "sha");
//		sb = repl(sb, "sh.a.", "sha");
		sb = repl(sb, "delni.{0,2}ka dru.{0,2}ba", "dd");
//		sb = repl(sb, "d.d.", "dd");
//		sb = repl(sb, "d.d", "dd");
		sb = repl(sb, "deoni.{0,2}arsko dru.{0,2}vo", "dd");
		sb = repl(sb, "akcionarsko dru.{0,2}vo", "ad");
//		sb = repl(sb, "a.d", "ad");
		sb = repl(sb, "research and development", "r&d");
		sb = repl(sb, "gesellschaft b.{0,2}rgerlichen rechts", "gbr");
		sb = repl(sb, "offene handelsgesellschaft", "ohg");
		sb = repl(sb, "benefit corporation", "b corporation");
		sb = repl(sb, "brothers", "bros");
		sb = repl(sb, "company", "co");
		sb = repl(sb, "incorporated", "inc");
		sb = repl(sb, "ingenieur", "ing");
		sb = repl(sb, "general", "gen");
		sb = repl(sb, "division", "div");
		sb = repl(sb, "corporated", "corp");
		sb = repl(sb, "limited", "ltd");
		sb = repl(sb, "labor\\w.*", "lab");
		
//		sb = repl(sb, "á", "a");
//		sb = repl(sb, "é", "e");
//		sb = repl(sb, "è", "e");
//		sb = repl(sb, "ô", "o");
//		sb = repl(sb, "ß", "ss");
//		sb = repl(sb, "ä", "ae");
//		sb = repl(sb, "ö", "oe");
//		sb = repl(sb, "ü", "ue");

		return sb.toString();
	}
}
