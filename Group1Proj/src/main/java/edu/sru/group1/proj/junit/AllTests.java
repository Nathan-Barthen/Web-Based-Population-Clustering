package edu.sru.group1.proj.junit;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;



//Contains 1 intentional failure.
@RunWith(Suite.class)
@SuiteClasses({ CountyTest.class, ShapefileTest.class, DBScanAndClustersTest.class, 
				SchoolInformationTest.class, SchoolClusteringTest.class, AddrSelectionTest.class })
/**
 * Used for junit Testing.
 * @author Connor
 *
 */
public class AllTests {

}
