package com.ym.orika;


public class Orika_PersonDto_Person_Mapper311461279843282$0 extends ma.glasnost.orika.impl.GeneratedMapperBase {

    public void mapAtoB(java.lang.Object a, java.lang.Object b, ma.glasnost.orika.MappingContext mappingContext) {


        super.mapAtoB(a, b, mappingContext);


// sourceType: Person
        com.ym.orika.Person source = ((com.ym.orika.Person)a);
// destinationType: PersonDto
        com.ym.orika.PersonDto destination = ((com.ym.orika.PersonDto)b);


        java.util.ArrayList new_destinationPersonalNames0 = null;
        if (!(((java.util.List)source.getNames()) == null)) {
            new_destinationPersonalNames0 = ((java.util.ArrayList)new java.util.ArrayList());
        } else {
            new_destinationPersonalNames0 = null;}
        ma.glasnost.orika.MapEntry personalNames_destination0Element = null;
        boolean personalNames_destination0ElementShouldBeAddedToCollector = false;
        if ( !(((java.util.List)source.getNames()) == null)) {

            java.util.Iterator names_$_iter = ((java.util.List)source.getNames()).iterator();
            while (names_$_iter.hasNext()) {
                com.ym.orika.Name names_source0Element = ((com.ym.orika.Name)names_$_iter.next());
                if ( personalNames_destination0Element == null || !((((java.lang.String)names_source0Element.getFirst()).equals(((com.ym.orika.Name)personalNames_destination0Element.getValue())))) || !(((((java.lang.String)names_source0Element.getFullName()) != null && ((java.lang.String)names_source0Element.getFullName()).equals(((java.lang.String)personalNames_destination0Element.getKey())))))) {

                    personalNames_destination0Element = ((ma.glasnost.orika.MapEntry)((ma.glasnost.orika.BoundMapperFacade)usedMapperFacades[0]).newObject(names_source0Element, mappingContext));
                    personalNames_destination0ElementShouldBeAddedToCollector = true;
                }
                mappingContext.beginMapping(((ma.glasnost.orika.metadata.Type)usedTypes[0]), ((java.util.List)source.getNames()), ((ma.glasnost.orika.metadata.Type)usedTypes[1]), new_destinationPersonalNames0);
                try {

                    if((personalNames_destination0Element == null)) {

                        personalNames_destination0Element = ((ma.glasnost.orika.MapEntry)((ma.glasnost.orika.BoundMapperFacade)usedMapperFacades[0]).newObject(names_source0Element, mappingContext));
                    }
                    personalNames_destination0Element.setKey(((java.lang.String)names_source0Element.getFullName()));
                    if (personalNames_destination0ElementShouldBeAddedToCollector) {
                        new_destinationPersonalNames0.add(((ma.glasnost.orika.MapEntry)personalNames_destination0Element));
                        personalNames_destination0ElementShouldBeAddedToCollector = false;
                    }
                    if((personalNames_destination0Element == null)) {

                        personalNames_destination0Element = ((ma.glasnost.orika.MapEntry)((ma.glasnost.orika.BoundMapperFacade)usedMapperFacades[0]).newObject(names_source0Element, mappingContext));
                    }
                    if ( !(((java.lang.String)names_source0Element.getFirst()) == null)) {  if ( ((com.ym.orika.Name)personalNames_destination0Element.getValue()) == null)  { personalNames_destination0Element.setValue((com.ym.orika.Name)((ma.glasnost.orika.BoundMapperFacade)usedMapperFacades[1]).map(((java.lang.String)names_source0Element.getFirst()), mappingContext)); } else { personalNames_destination0Element.setValue((com.ym.orika.Name)((ma.glasnost.orika.BoundMapperFacade)usedMapperFacades[1]).map(((java.lang.String)names_source0Element.getFirst()), ((com.ym.orika.Name)personalNames_destination0Element.getValue()), mappingContext)); }   }  else {
                        { personalNames_destination0Element.setValue(null); }
                    }

                } finally {
                    mappingContext.endMapping();
                }

            }}

        if (!(new_destinationPersonalNames0 == null) && !new_destinationPersonalNames0.isEmpty()) {
            if (((java.util.Map)destination.getPersonalNames()) == null) {
                destination.setPersonalNames(((java.util.Map)new java.util.LinkedHashMap()));
            } else {

                ((java.util.Map)destination.getPersonalNames()).clear();
            }

            listToMap(new_destinationPersonalNames0, ((java.util.Map)destination.getPersonalNames()));
        }

        if(customMapper != null) {
            customMapper.mapAtoB(source, destination, mappingContext);
        }
    }

    public void mapBtoA(java.lang.Object a, java.lang.Object b, ma.glasnost.orika.MappingContext mappingContext) {


        super.mapBtoA(a, b, mappingContext);


// sourceType: PersonDto
        com.ym.orika.PersonDto source = ((com.ym.orika.PersonDto)a);
// destinationType: Person
        com.ym.orika.Person destination = ((com.ym.orika.Person)b);


        java.util.List new_destinationNames0 = null;
        if (!(((java.util.Map)source.getPersonalNames()) == null)) {
            new_destinationNames0 = ((java.util.List)new java.util.ArrayList());
        } else {
            new_destinationNames0 = null;}
        com.ym.orika.Name names_destination0Element = null;
        boolean names_destination0ElementShouldBeAddedToCollector = false; if ( !(((java.util.Map)source.getPersonalNames()) == null)) {

            java.util.Iterator personalNames_$_iter = ((java.util.Map)source.getPersonalNames()).entrySet().iterator();
            while (personalNames_$_iter.hasNext()) {
                java.util.Map.Entry personalNames_source0Element = ((java.util.Map.Entry)personalNames_$_iter.next());
                if ( names_destination0Element == null || !(((((java.lang.String)names_destination0Element.getFirst()) != null && ((java.lang.String)names_destination0Element.getFirst()).equals(((ma.glasnost.orika.Converter)usedConverters[0]).convert(((com.ym.orika.Name)personalNames_source0Element.getValue()), ((ma.glasnost.orika.metadata.Type)usedTypes[2]), mappingContext))))) || !(((((java.lang.String)personalNames_source0Element.getKey()) != null && ((java.lang.String)personalNames_source0Element.getKey()).equals(((java.lang.String)names_destination0Element.getFullName())))))) {

                    names_destination0Element = ((com.ym.orika.Name)((ma.glasnost.orika.BoundMapperFacade)usedMapperFacades[2]).newObject(personalNames_source0Element, mappingContext));
                    names_destination0ElementShouldBeAddedToCollector = true;
                }
                mappingContext.beginMapping(((ma.glasnost.orika.metadata.Type)usedTypes[3]), ((java.util.Map)source.getPersonalNames()), ((ma.glasnost.orika.metadata.Type)usedTypes[0]), new_destinationNames0);
                try {

                    if((names_destination0Element == null)) {

                        names_destination0Element = ((com.ym.orika.Name)((ma.glasnost.orika.BoundMapperFacade)usedMapperFacades[2]).newObject(personalNames_source0Element, mappingContext));
                    }
                    names_destination0Element.setFullName(((java.lang.String)personalNames_source0Element.getKey()));
                    if (names_destination0ElementShouldBeAddedToCollector) {
                        new_destinationNames0.add(((com.ym.orika.Name)names_destination0Element));
                        names_destination0ElementShouldBeAddedToCollector = false;
                    }
                    if((names_destination0Element == null)) {

                        names_destination0Element = ((com.ym.orika.Name)((ma.glasnost.orika.BoundMapperFacade)usedMapperFacades[2]).newObject(personalNames_source0Element, mappingContext));
                    }
                    if ( !(((com.ym.orika.Name)personalNames_source0Element.getValue()) == null)){
                        names_destination0Element.setFirst(((java.lang.String)((ma.glasnost.orika.Converter)usedConverters[0]).convert(((com.ym.orika.Name)personalNames_source0Element.getValue()), ((ma.glasnost.orika.metadata.Type)usedTypes[2]), mappingContext)));
                    } else {
                        names_destination0Element.setFirst(null);
                    }
                } finally {
                    mappingContext.endMapping();
                }

            }}

        if (!(new_destinationNames0 == null) && !new_destinationNames0.isEmpty()) {
            if (((java.util.List)destination.getNames()) == null) {
                destination.setNames(((java.util.List)new java.util.ArrayList()));
            } else {

                ((java.util.List)destination.getNames()).clear();
            }

            ((java.util.List)destination.getNames()).addAll(new_destinationNames0);
        }

        if(customMapper != null) {
            customMapper.mapBtoA(source, destination, mappingContext);
        }
    }}
