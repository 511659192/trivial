package com.ym.orika;


public class Orika_MapEntry_Name_Mapper311461289486528$1 extends ma.glasnost.orika.impl.GeneratedMapperBase {

    public void mapAtoB(java.lang.Object a, java.lang.Object b, ma.glasnost.orika.MappingContext mappingContext) {


        super.mapAtoB(a, b, mappingContext);


// sourceType: Name
        com.ym.orika.Name source = ((com.ym.orika.Name)a);
// destinationType: MapEntry<String, Name>
        ma.glasnost.orika.MapEntry destination = ((ma.glasnost.orika.MapEntry)b);


        destination.setKey(((java.lang.String)source.getFullName()));
        if ( !(((java.lang.String)source.getFirst()) == null)) {  if ( ((com.ym.orika.Name)destination.getValue()) == null)  { destination.setValue((com.ym.orika.Name)((ma.glasnost.orika.BoundMapperFacade)usedMapperFacades[0]).map(((java.lang.String)source.getFirst()), mappingContext)); } else { destination.setValue((com.ym.orika.Name)((ma.glasnost.orika.BoundMapperFacade)usedMapperFacades[0]).map(((java.lang.String)source.getFirst()), ((com.ym.orika.Name)destination.getValue()), mappingContext)); }   }  else {
            { destination.setValue(null); }
        }

        if(customMapper != null) {
            customMapper.mapAtoB(source, destination, mappingContext);
        }
    }

    public void mapBtoA(java.lang.Object a, java.lang.Object b, ma.glasnost.orika.MappingContext mappingContext) {


        super.mapBtoA(a, b, mappingContext);


// sourceType: MapEntry<String, Name>
        ma.glasnost.orika.MapEntry source = ((ma.glasnost.orika.MapEntry)a);
// destinationType: Name
        com.ym.orika.Name destination = ((com.ym.orika.Name)b);


        destination.setFullName(((java.lang.String)source.getKey()));
        if ( !(((com.ym.orika.Name)source.getValue()) == null)){
            destination.setFirst(((java.lang.String)((ma.glasnost.orika.Converter)usedConverters[0]).convert(((com.ym.orika.Name)source.getValue()), ((ma.glasnost.orika.metadata.Type)usedTypes[0]), mappingContext)));
        } else {
            destination.setFirst(null);
        }
        if(customMapper != null) {
            customMapper.mapBtoA(source, destination, mappingContext);
        }
    }
}
