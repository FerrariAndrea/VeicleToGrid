package persistence;

import java.util.List;

public class SibReader {
	public static void main(String[] args)
	{
		KPConnector kp = KPConnector.GetInstance();
		try {
			kp.join();
			//List<Triple> triples = kp.query(null, null, null,null,null);	
			List<Triple> triples =  kp.querySelect_S_P_O_Where("Select ?a ?b ?c where { ?a ?b ?c . FILTER (isURI(?a) && STRSTARTS(str(?a), str('http://ola') ) ) }");
			for( int i=0;i<triples.size() ;i++) {
				System.out.println(triples.get(i).toString());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
