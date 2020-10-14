import java.util.*;

public class HashListAutocomplete implements Autocompletor{
    private static final int MAX_PREFIX = 10;
    private Map<String,List<Term>> myMap;
    private int mySize;

    public HashListAutocomplete(String[] terms, double[] weights) {
        if (terms == null || weights == null) {
            throw new NullPointerException("One or more arguments null");
        }
        myMap = new HashMap<>();
        initialize(terms,weights);
    }

    @Override
    public List<Term> topMatches(String prefix, int k) {
        if (prefix.length() > MAX_PREFIX)
            prefix = prefix.substring(0,MAX_PREFIX);
        if (!myMap.containsKey(prefix))
            return new ArrayList<>();
        List<Term> all = myMap.get(prefix);
        return all.subList(0,Math.min(k,all.size()));
    }

    @Override
    public void initialize(String[] terms, double[] weights) {
        myMap.clear();
        for (int j=0;j<terms.length;j++) {
            for (int i = 0; i<MAX_PREFIX+1 && i<terms[j].length()+1; i++) {
                String prefix = terms[j].substring(0,i);
                myMap.putIfAbsent(prefix,new ArrayList<>());
                myMap.get(prefix).add(new Term(terms[j], weights[j]));
            }
        }
        for (String key : myMap.keySet()) {
            List<Term> list = myMap.get(key);
            Collections.sort(list,Comparator.comparing(Term::getWeight).reversed());
        }
    }

    @Override
    public int sizeInBytes() {
        if (mySize == 0) {
            for(String key : myMap.keySet()) {
                mySize += key.length()*BYTES_PER_CHAR;
                for (Term t:myMap.get(key))
                    mySize += t.getWord().length()*BYTES_PER_CHAR+BYTES_PER_DOUBLE;
            }
        }
        return mySize;
    }

}
