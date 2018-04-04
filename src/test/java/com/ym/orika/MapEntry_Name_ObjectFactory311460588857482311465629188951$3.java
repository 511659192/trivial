package com.ym.orika;


public class MapEntry_Name_ObjectFactory311460588857482311465629188951$3 extends ma.glasnost.orika.impl.GeneratedObjectFactory {

    public Object create(Object s, ma.glasnost.orika.MappingContext mappingContext) {if(s == null) throw new java.lang.IllegalArgumentException("source object must be not null");if (s instanceof com.ym.orika.Name) {com.ym.orika.Name source = (com.ym.orika.Name) s;
        try {

            java.lang.Object arg0 = null; if ( !(((java.lang.String)source.getFullName()) == null)){
                arg0 = ((java.lang.String)source.getFullName());
            }
            else {
                arg0 = null;
            }
            java.lang.Object arg1 = null; if ( !(((java.lang.String)source.getFirst()) == null)){
                arg1 = ((java.lang.String)source.getFirst());
            }
            else {
                arg1 = null;
            }return new ma.glasnost.orika.MapEntry(arg0,arg1);
        } catch (java.lang.Exception e) {

            if (e instanceof RuntimeException) {

                throw (RuntimeException)e;

            } else {
                throw new java.lang.RuntimeException("Error while constructing new MapEntry instance", e);
            }
        }
    }return new ma.glasnost.orika.MapEntry();
    }}
