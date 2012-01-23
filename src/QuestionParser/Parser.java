/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package QuestionParser;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Dinu
 */
public class Parser {

    static 
    {
        loadKeywords();
    }


//persoane, numere, masuri, locatii, timp, organizatii, obiecte
    static final String[]wordnet =
    {
        "(scorer|director|President|spouses|wife|writer|actor|artist\\s|singer|historian|thinker|author|mortal|immortal|member.?|partner.?|goddess|representatives|disciple)",
        "(position.?|age|percentage|percent|number.?)",
        "(area|height|mass|masses|amplitude|duration|depth|speed|length|quantity|size|density)",
        "((region|district|Capital|island|Peak|territory|province|continent|state|street|avenue|boulevard|county|republic)(s?))|((Cit|countr|count)(y|ies))",
        "(year(s?)|centur(y|ies)|period(s?)|date(s?)|time|hour)",
        
//        "(organization|Organizers|agents|University|group|company|companies|entity|entities|Manufacturer|institution.?|prison.?|Order.?|Party|CIA|Agency|UNESCO|Eesti|Solidarity|Ethers|led|NATO|European Union|EU)",
//        "(a|an)?\\s?(?>instrument|product|gun.?|machine|middle|unit|concrete-wood|equipment|toy.?|compass|compasses|armor.?|equipment|computer|device|Selena|stole)"
    };
    
//    static final String[][] q_types = {
//        {"What's the procedure", "What is the procedure", "What are the stages", "What are the Community's procedures",
//            "What  measures", "What are the Community's procedures for","Under what circumstances", "In what circumstances"},
//        {"Why", "By what reason", "For what reason", "What is the motive", "What is  the main reason", "On what ground"},
//        {"What is the purpose", "What's the purpose", "To what purpose", "On what purpose", "For what purpose",
//            "What's the aim", "/What is the aim", "What's the objective", "What is the objective", "What are the goals",
//            "What are the objectives", "What are the Community's objectives", "What are the aims", "What is the scope"},
//        {"What means\\s+[A-Z]", "What is (a|an)", "What are the", "Who (is|was)\\s+[A-Z]", "What does the term",
//            "What is meant by", "What(is|was|are)\\s+(?!(?>a|an)\\s)", "What is the meaning", "What is the definition"},
//        {"What types", "Who were", "Whom", "Through what", "Name (?!(?>a|an)\\s)"}
//    };
    
//    static final String[][] a_types = {{"(Why|What|Name|With who|What is called)", // .*?+wordnet[0],
//        "What is (his|her) name", "Who", "Whom", "Whos", "With who"},
//            {"Approximately how many","Of how many", "How many", "How much", "Of how much", "(What|Who is)\\s"+ wordnet[1]},
//            {"How (much|manny) ", "(What is the).*?"+wordnet[2]},
//            {"What (state|city)", "From where", "Where", "(On what|What|On which|Name).*?"+wordnet[3]},
//            {"When", "(In what|From what|At what|After how (much|many)) "+wordnet[4]},
//            {"Who produced", "Who made", "(What was the|What is the|At|From what|What was|What is).*?(?<!of\\s)"+wordnet[5]},
//            {"What ", "What (give|gived|gives)", "With what(he|she|it) ", "(What|Name|What is the name of the|For what|At what).*?"+wordnet[6]}
//    };

    static final String[][] a_types = {{"(Why|What|Name|With who|What is called)", // .*?+wordnet[0],
            "What is (his|her) name", "Who", "Whom", "Whos", "With who"},
        {"What (state|city)", "From where", "Where", "(On what|What|On which|Name).*?" + wordnet[3]},
        {"When", "(In what|From what|At what|After how (much|many)) " + wordnet[4]},
        {"Who produced", "Who made", "(What was the|What is the|At|From what|What was|What is).*?(?<!of\\s)"},
        {"What ", "What (give|gived|gives)", "With what(he|she|it) ", "(What|Name|What is the name of the|For what|At what).*?"},

        {"Approximately how many", "Of how many", "Of how much", "(What|Who is)\\s" + wordnet[1],
        "How (much|manny) ", "(What is the) " + wordnet[2]}
    };

    
//    static String[] a_typesTitle = {"PERSON", "COUNT", "MEASURE", "LOCATION", "TIME", "ORGANIZATION", "OBJECT"};
    static String[] a_typesTitle = {"PERSON", "LOCATION", "TIME", "ORGANIZATION", "OBJECT", "QUANTITY"};
    
//    static String[] q_typesTitle = {"PROCEDURE", "RESON", "PURPOSE", "DEFINITION", "LIST"};
    
    public static Question parse(String question)
    {
        Question outputQuestion = new Question(question);
//
//        Match match = new Match();
//        Pattern r = Pattern.compile(a_types[0][0]);
//
        boolean found = false;
//        for(int i=0; i<a_types.length; i++)
//        {
//            for(String pattern : a_types[i])
//            {
//                if(Pattern.matches(pattern, question))
//                {
//                    outputQuestion.setAnswerType(a_typesTitle[i]);
//                    found = true;
//                    break;
//                }
//            }
//            if(found)
//                break;
//        }
//
//        found = false;
//        for(int i=0; i<q_types.length; i++)
//        {
//            for(String pattern : q_types[i])
//            {
//                if(Pattern.matches(pattern, question))
//                {
//                    outputQuestion.setQuestionType(q_typesTitle[i]);
//                    found = true;
//                    break;
//                }
//            }
//            if(found)
//                break;
//        }
//
//        findKeywords(outputQuestion);
        
        
        String[] patterns = {"^What (is|are) ", "^What ", "^Who (is|are) ", "^Where ", "^When (does|did|do)?", "^What (does|did) (\\w+ \\w+) do "};
        String[] answerType = {"Definition", "Object", "Person", "Location", "Time", "Occupation"};
        
        
        
//        for(int i=0; i<patterns.length; i++)
//        {
//            Pattern p = Pattern.compile(patterns[i]);
//            Matcher m = p.matcher(question);
//            if(m.find())
//            {
//                outputQuestion.setAnswerType(answerType[i]);
//                
//                break;
//            }
//        }


        
        Pattern novelP = Pattern.compile("\\s*the? novel");
        Pattern personP = Pattern.compile("\\s*(the)?\\s*(([A-Z]\\w+\\s*)+)");
//        Pattern mainCharP = Pattern.compile("");
        
        String additionalP = "\\s*((the? novel)|((the)?\\s*(([A-Z]\\w+\\s*)+)))?";
        
        Pattern p = Pattern.compile("^What (is|are) " + additionalP);
        Matcher m = p.matcher(question);
        if (m.find()) {
            outputQuestion.setAnswerType(AnswerType.DEFINITION);
            if (m.group(1) != null) {
                if (m.group(1).toUpperCase().equals("IS")) {
                    outputQuestion.setMulitplicity(Multiplicity.MULTIPLE);
                } else if (m.group(1).toUpperCase().equals("ARE")) {
                    outputQuestion.setMulitplicity(Multiplicity.SINGLE);
                }
            }
            
            int start = 2;
            if(m.groupCount() >= start && m.group(start) != null)
            {
                if (m.group(start+1) != null) {
                    outputQuestion.addFocusType(FocusType.NOVEL);
//                    question = question.replace(m.group(start + 1), "");
                } else 
                if (m.group(start+2) != null) {
                    if (m.group(start + 3) != null) {
                        outputQuestion.addFocusType(FocusType.OBJECT);
                    } else {
                        outputQuestion.addFocusType(FocusType.PERSON);
                    }
                    outputQuestion.addMainObject(m.group(start + 4));
//                    question = question.replace(m2.group(0), "");
                }
            }
            
            
//            Matcher m2 = novelP.matcher(question);
//            if(m2.find(m.end()))
//            {
//                outputQuestion.addFocusType(FocusType.NOVEL);
//                question = question.replace(m2.group(0), "");
//            }
//            else
//            {
//                m2 = personP.matcher(question);
//                if (m2.find(m.end())) {
//                    if(m2.group(1) != null)
//                        outputQuestion.addFocusType(FocusType.OBJECT);
//                    else
//                        outputQuestion.addFocusType(FocusType.PERSON);
//                    outputQuestion.addMainObject(m2.group(2)); 
//                    question = question.replace(m2.group(0), "");
//                }
//            }

            
            
            question = question.replace(m.group(0), ""); 
            
//            m2 = mainCharP.matcher(question);
//            if (m2.find(m.end())) {
//                outputQuestion.setFocus("main character");
//            }
        }
        else
        {
            p = Pattern.compile("^What (\\w+) (is|are) " + additionalP);
            m = p.matcher(question);
            if (m.find()) {
                outputQuestion.setAnswerType(AnswerType.DEFINITION);

                if (m.group(2) != null) {
                    if (m.group(2).toUpperCase().equals("IS")) {
                        outputQuestion.setMulitplicity(Multiplicity.MULTIPLE);
                    } else if (m.group(2).toUpperCase().equals("ARE")) {
                        outputQuestion.setMulitplicity(Multiplicity.SINGLE);
                    }
                }


                int start = 3;
                if (m.groupCount() >= start && m.group(start) != null) {
                    if (m.group(start + 1) != null) {
                        outputQuestion.addFocusType(FocusType.NOVEL);
//                    question = question.replace(m.group(start + 1), "");
                    } else if (m.group(start + 2) != null) {
                        if (m.group(start + 3) != null) {
                            outputQuestion.addFocusType(FocusType.OBJECT);
                        } else {
                            outputQuestion.addFocusType(FocusType.PERSON);
                        }
                        outputQuestion.addMainObject(m.group(start + 4));
//                    question = question.replace(m2.group(0), "");
                    }
                }
                //retaining word
                question = m.group(1) + " " + question.replace(m.group(0), "");
            }
            else
            {
                p = Pattern.compile("^What ((does|did|do)|(\\w+)) " + additionalP );
                m = p.matcher(question);
                if (m.find()) {
//                    outputQuestion.setAnswerType(AnswerType.EXPLANATION);
                    if (m.group(2) != null) {
                        outputQuestion.setAnswerType(AnswerType.DEFINITION);

//                        if (m.group(1).toUpperCase().equals("IS")) {
//                            outputQuestion.setMulitplicity(Multiplicity.MULTIPLE);
//                        } else if (m.group(1).toUpperCase().equals("ARE")) {
//                            outputQuestion.setMulitplicity(Multiplicity.SINGLE);
//                        }
                    }
                    else
                        outputQuestion.setAnswerType(AnswerType.OBJECT);
                        



                    int start = 4;
                    if (m.groupCount() >= start && m.group(start) != null) {
                        if (m.group(start + 1) != null) {
                            outputQuestion.addFocusType(FocusType.NOVEL);
                        } else if (m.group(start + 2) != null) {
                            if (m.group(start + 3) != null) {
                                outputQuestion.addFocusType(FocusType.OBJECT);
                            } else {
                                outputQuestion.addFocusType(FocusType.PERSON);
                            }
                            outputQuestion.addMainObject(m.group(start + 4));
                        }
                    }

                    question = question.replace(m.group(0), "");
                    if(m.group(3) != null)
                        question = m.group(3) + " " + question;
                }
            }
        }

        p = Pattern.compile("^Who ((is|are)|(\\w+)) " + additionalP);
        m = p.matcher(question);
        if (m.find()) {
            outputQuestion.setAnswerType(AnswerType.PERSON);
            if (m.group(2) != null) {
                if (m.group(1).toUpperCase().equals("IS")) {
                    outputQuestion.setMulitplicity(Multiplicity.MULTIPLE);
                } else if (m.group(1).toUpperCase().equals("ARE")) {
                    outputQuestion.setMulitplicity(Multiplicity.SINGLE);
                }
            }
            
//            FocusType defaultFocus = FocusType.PERSON;
//            if(m.group(3))
                
            int start = 4;
            if (m.groupCount() >= start && m.group(start) != null) {
                if (m.group(start + 1) != null) {
                    outputQuestion.addFocusType(FocusType.NOVEL);
                } else if (m.group(start + 2) != null) {
                    //Becouse we find name 
                    if(m.group(2) != null) //other verb
                        outputQuestion.setAnswerType(AnswerType.DEFINITION);
                    if (m.group(start + 3) != null) {
                        outputQuestion.addFocusType(FocusType.OBJECT);
                    } else {
                        outputQuestion.addFocusType(FocusType.PERSON);
                    }
                    outputQuestion.addMainObject(m.group(start + 4));
                }
            }

            question = question.replace(m.group(0), "");
            if (m.group(3) != null) {
                question = m.group(3) + " " + question;
            }
        }

        p = Pattern.compile("^Where ((is|are|does|did|do)|(\\w+)) " + additionalP);
        m = p.matcher(question);
        if (m.find()) {
            outputQuestion.setAnswerType(AnswerType.LOCATION);
            if (m.group(2) != null) {
                if (m.group(2).toUpperCase().equals("IS")) {
                    outputQuestion.setMulitplicity(Multiplicity.MULTIPLE);
                } else if (m.group(2).toUpperCase().equals("ARE")) {
                    outputQuestion.setMulitplicity(Multiplicity.SINGLE);
                }
            }
            
            
            int start = 4;
            if (m.groupCount() >= start && m.group(start) != null) {
                if (m.group(start + 1) != null) {
                    outputQuestion.addFocusType(FocusType.NOVEL);
                } else if (m.group(start + 2) != null) {
                    if (m.group(start + 3) != null) {
                        outputQuestion.addFocusType(FocusType.OBJECT);
                    } else {
                        outputQuestion.addFocusType(FocusType.PERSON);
                    }
                    outputQuestion.addMainObject(m.group(start + 4));
                }
            }

            question = question.replace(m.group(0), "");
        }
        
        p = Pattern.compile("^When ((does|did|do|is|are)|(\\w+))" + additionalP);
        m = p.matcher(question);
        if (m.find()) {
            outputQuestion.setAnswerType(AnswerType.TIME);
            if(m.group(2) != null)
            {
                if (m.group(2).toUpperCase().equals("IS")) {
                    outputQuestion.setMulitplicity(Multiplicity.MULTIPLE);
                } else if (m.group(2).toUpperCase().equals("ARE")) {
                    outputQuestion.setMulitplicity(Multiplicity.SINGLE);
                }
            }
            
            
            int start = 4;
            if (m.groupCount() >= start && m.group(start) != null) {
                if (m.group(start + 1) != null) {
                    outputQuestion.addFocusType(FocusType.NOVEL);
                } else if (m.group(start + 2) != null) {
                    if (m.group(start + 3) != null) {
                        outputQuestion.addFocusType(FocusType.OBJECT);
                    } else {
                        outputQuestion.addFocusType(FocusType.PERSON);
                    }
                    outputQuestion.addMainObject(m.group(start + 4));
                }
            }

            question = question.replace(m.group(0), "");
        }
        
        p = Pattern.compile("^How (does|did|do|is|are) " + additionalP);
        m = p.matcher(question);
        if (m.find()) {
            outputQuestion.setAnswerType(AnswerType.MODALITY);

            //TODO add word to ...

            if (m.group(1) != null) {
                if (m.group(1).toUpperCase().equals("IS")) {
                    outputQuestion.setMulitplicity(Multiplicity.MULTIPLE);
                } else if (m.group(1).toUpperCase().equals("ARE")) {
                    outputQuestion.setMulitplicity(Multiplicity.SINGLE);
                }
            }


            int start = 2;
            if (m.groupCount() >= start && m.group(start) != null) {
                if (m.group(start + 1) != null) {
                    outputQuestion.addFocusType(FocusType.NOVEL);
                } else if (m.group(start + 2) != null) {
                    if (m.group(start + 3) != null) {
                        outputQuestion.addFocusType(FocusType.OBJECT);
                    } else {
                        outputQuestion.addFocusType(FocusType.PERSON);
                    }
                    outputQuestion.addMainObject(m.group(start + 4));
                }
            }

            question = question.replace(m.group(0), "");
        }
        
        
//        String text = question;
        Pattern r = Pattern.compile("([\"\'](\\w)+[\"\'])|(\\s*(([A-Z]\\w+\\s*)+))");
        m = r.matcher(question);
        int end = 0;
        while (m.find(end)) {
            if(m.group(2) != null)
            {
                outputQuestion.addMainObject(m.group(2).trim());  //TODO changed
                question = question.replace(m.group(2), "");
            }
            else
            {
                outputQuestion.addMainObject(m.group(4).trim());
                question = question.replace(m.group(4), "");
            }
            end = m.end();
        }
        
       
        setKeywords(outputQuestion, question);

        
        return outputQuestion;
    }
    
    public static void setKeywords(Question outputQuestion, String question)
    {
        
        question = question.replaceAll("[\\?,.!]", " ");

        //Filtering
        question = question.trim().replaceAll("(^|\\W)(the|&|or|and)(\\W|$)", " ");
        
        if(question.equals(""))
        {
            outputQuestion.setKeywords(new String[0]);
            return;
        }
        
        String[] words = question.split("\\s+");
        int[] join = new int[words.length];
        Arrays.fill(join, -1);
//        int counter = 0;
        boolean joined = false;
        
//        for(String word : words)
        String word, nextWord;
        for(int i=0; i< words.length; i++)
        {
            word = words[i];
           if(wordType != null && wordType.containsKey(word) &&
               wordType.get(word) == 'J') //Adjective
           {
               //and next is noun or unknown join
               
               if(i+1<words.length )
               {
                   nextWord = words[i+1];
                   if (!wordType.containsKey(nextWord) || wordType.get(nextWord) == 'N')
                   {
                       joined = true;
//                       counter++;
//                       join[i] = counter;
                       join[i+1] = i;
                       i++;
                   }
               }
           }
        }
        
        
        
        if(joined)
        {
            String[] outputWords = new String[words.length-1];
            int counter = 0;
//            int n = words.length;
            for(int i=0; i< words.length; i++)
            {
                if(join[i] < 0)
                {
                    outputWords[i - counter] = words[i];
                }
                else
                {
                    outputWords[join[i]-counter] += " " + words[i];
                    counter++;
                }
            }
            words = outputWords;
        }
        
        
        
//        words = filterWords(words);
        outputQuestion.setKeywords(words);
    }
    
    
//    public void findKeywords(Question question)
//    {
//        ArrayList list = new ArrayList();
//        
//        String text = question.getOriginalText();
//        Pattern r = Pattern.compile("([\"\'](\\w)+[\"\'])|(\\s(([A-Z]\\w+\\s*)+))");
//        Matcher m = r.matcher(text);
//        int end = 0;
//        while (m.find(end)) {
////            System.out.println("2: " + m.group(2));
////            System.out.println("4: " + m.group(4));
//            
////            if(m.group(2) != null)
////                question.addFocus(m.group(2).trim());  //TODO changed
////            else
////                question.addFocus(m.group(4).trim());
////            end = m.end();
//        }
//        //TODO if not found focus set first noun.
//        
////        r = Pattern.compile("Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday|January|February|March|April|June|July|August|September|October|November|December");
////        m = r.matcher(text);
////        end = 0;
////        while (m.find(end)) {
//////            System.out.println("Zorro: " + m.group(0));
////            list.add(m.group(0)); //emphisize words
////            end = m.end();
////        }
//        
////        System.out.println("words count: " + text.split(" ").length);
//        
//        
//        text = text.replaceAll("[\\?,.!]", text);
//        
//        String[] words = text.split("\\s+");
//        
//        //TODO filter words
//        
//        question.setKeywords(words);
//        
//        //TODO continue parsing
//    }
    
    static HashMap<String, Character> wordType;
    private static void loadKeywords()
    {
        if(wordType != null)
            return;
        wordType = new HashMap<String, Character>();
        Scanner scanner = null;
        String last = "";
        char type= 'A';
        Pattern r = Pattern.compile("\"(\\w+)\"");
        Matcher m = null;
        int end = 0;
        try {
            scanner = new Scanner(new FileInputStream("./lemmeEN.txt"));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                m = r.matcher(line);

                end = 0;
                
                type = line.charAt(0);
                while (m.find(end) && !m.group(1).equals(last)) {
                    last = m.group(1);
                    wordType.put(m.group(1), type);
                    end = m.end();
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }
}


