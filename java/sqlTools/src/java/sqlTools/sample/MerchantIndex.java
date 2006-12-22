
package sqlTools.sample;

import utils.*;
import sqlTools.orm.*;

public class MerchantIndex extends CmdLineOrm {


	public String merchantId;
	public String waehrungIso;
	public int id;
	public String clearingBrand;
	public String brandId;
	public MerchantZvt zvt;

	public static MerchantIndex loadMerchantIndex(String brand, String merchantId, String curr) {
		Object [] parameter = {
			brand, merchantId, curr	
		};
		return (MerchantIndex)load (MerchantIndex.class, parameter);
	}

	public OneToOneRelation [] getOneToOneRelations() {
		try {
			if (rels == null) {
				rels = new OneToOneRelation[1];
				final ORMField [] of = new ORMField[1];
				of[0]=getField("id");

				rels[0] = new OneToOneRelation (this.getClass().getDeclaredField("zvt")){
					public ORMField [] getConnectingFields (){
						return of;	
					}
				};

			}
		} catch (NoSuchFieldException nsfe) {
			nsfe.printStackTrace();
			throw new RuntimeException("[MerchantIndex.getOneToOneRelations] Fuck! Arse!");
		}
	
		return rels;
	} 
	private OneToOneRelation [] rels;

	public String toString () {
		StringBuffer buf = new StringBuffer();
		buf.append (super.toString());
		buf.append (" \t contains: ");
		buf.append (zvt.toString());
		return buf.toString();
	}

	public static void main (String [] args) {
		MerchantIndex.setCmdLine(new DBCmdLine(args));
		
		MerchantIndex idx = new MerchantIndex();
//		System.out.println(idx.getTableName());
//		ORMField [] fields = idx.getORMFields();
//		
//		System.out.println("Mapped Cols");
//		for (int i=0; i!=fields.length; ++i) {
//			System.out.println(fields[i]);	
//		}
//		
//		ORMField [] obj = idx.getPrimaryKeys();
//		System.out.println("Primary keys:");
//		for (int i = 0; i!=obj.length; ++i) {
//			System.out.println(obj[i].getName());	
//		}

		idx.brandId = "Visa";
		idx.merchantId = "tim_ohne_cvv";
		idx.waehrungIso = "978";

		//idx.loadFromDB();

		idx = loadMerchantIndex ("Visa", "tim_ohne_cvv", "978");
		System.out.println(idx);
		System.out.println(idx.zvt);
		boolean j = idx.insert();
		
		System.out.println("-----"+j+"------");

		ORMObject [] arr = MerchantIndex.loadWithWhere(MerchantIndex.class, "merchant_id like 'tim%'");
		for (int i=0; i!=arr.length; ++i) {
			System.out.println(arr[i]);	
		}

//		idx = new MerchantIndex();
//		idx.brandId = "Visa";
//		idx.merchantId = "gibts_noch_nicht";
//		idx.waehrungIso = "978";
//		idx.clearingBrand = arr[0].getString("clearingBrand");
//
//		idx.save();

		idx = MerchantIndex.loadMerchantIndex ("Visa", "gibts_noch_nicht", "978");
		System.out.println(idx);
		idx.id=840;
		idx.zvt.country=idx.zvt.country+1;
		idx.save();
		


	

		System.out.println (idx);

		

		
	}
}
