package uk.ac.ebi.phenotype.service;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by ilinca on 02/02/2017.
 */

public class PharosServiceTest {

    PharosService ps = new PharosService();

    @Test
    public void testGetPharosInfo(){

        PharosDTO pharosDTO = ps.getPharosInfo("MTOR");
        assertTrue (pharosDTO != null);
        assertTrue (pharosDTO.getIdg2() == 0 );
        assertTrue (pharosDTO.getTdl().equalsIgnoreCase("Tclin") );

    }


}
