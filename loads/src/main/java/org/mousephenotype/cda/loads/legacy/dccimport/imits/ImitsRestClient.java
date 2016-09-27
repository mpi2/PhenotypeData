package org.mousephenotype.cda.loads.legacy.dccimport.imits;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.glassfish.jersey.apache.connector.ApacheClientProperties;
import org.glassfish.jersey.apache.connector.ApacheConnector;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.filter.HttpBasicAuthFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static javax.ws.rs.client.ClientBuilder.newBuilder;


/**
 * encapsulates interactions with the iMits REST interface documented here:
 * https://github.com/mpi2/imits/wiki
 */
@Component
public class ImitsRestClient implements ImitsClient {

	private static final Logger LOG = LoggerFactory.getLogger(ImitsRestClient.class);
	private static final String IMITS_URL = "https://www.i-dcc.org/imits/";
	private static final int IMITS_TRIES = 6;

	private static Map<String, List<MicroInjectionAttemptBean>> miAttemptByEsCellCache = new ConcurrentHashMap<>();
    private static Map<String, MicroInjectionAttemptBean> miAttemptByEsCellAndCenterCache = new ConcurrentHashMap<>();
	private static Map<String, PhenotypeAttemptBean> phenotypeAttemptCache = new ConcurrentHashMap<>();
    private static Map<String, List<EsCellBean>> esCellCache = new ConcurrentHashMap<>();

	private Client client;
	private WebTarget wt;


	/**
	 * for use in dependency managed environments. This uses the production
	 * imits url
	 *
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	public ImitsRestClient() throws NoSuchAlgorithmException, KeyManagementException {
		this.initEndPoint(IMITS_URL, false);
	}


	/**
	 * constructor for a configurable REST endpoint iMits url
	 *
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	public ImitsRestClient(String endpoint) throws NoSuchAlgorithmException, KeyManagementException {
		this.initEndPoint(endpoint, false);
	}


	/**
	 * Configurable endpoint URL, username, and password.
	 *
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	public ImitsRestClient(String endpoint, String username, String password) throws NoSuchAlgorithmException, KeyManagementException {
		client = getClient();
		client.register(new HttpBasicAuthFilter(username, password));
		wt = client.target(endpoint);

	}


	/**
	 * returns a client either proxied or not depending on if the system
	 * has set a proxy
	 *
	 * @return a Client that either is or is not proxied as appropriate
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 */
	public Client getClient() throws KeyManagementException, NoSuchAlgorithmException {
		Client client;

		String proxyHost = null;
		Integer proxyPort;
		try {
			proxyHost = System.getProperty("http.proxyHost");
			proxyPort = Integer.parseInt(System.getProperty("http.proxyPort"));
		} catch (NumberFormatException e) {
			// proxy port is either not defined or not defined properly
			// default to 8080 just in case
			proxyPort = 8080;
		}

		if (proxyHost != null && !proxyHost.isEmpty()) {
			ClientConfig cc = new ClientConfig();
			cc.property(ApacheClientProperties.PROXY_URI, "http://" + proxyHost + ":" + proxyPort);
			client = newBuilder().sslContext(getSecurityManager()).newClient(cc.connector(new ApacheConnector(cc.getConfiguration())));
		} else {
			client = newBuilder().sslContext(getSecurityManager()).build();
		}

		return client;
	}


	/**
	 * Init connection to endpoint REST interface
	 *
	 * @param endpoint REST endpoint
	 * @param bReset   set to true if the client needs to be reinstanciated (in case of communication failure)
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	void initEndPoint(String endpoint, boolean bReset) throws NoSuchAlgorithmException, KeyManagementException {
		if (bReset && client != null) {
			client.close();
		}
		client = getClient();
		client.register(new HttpBasicAuthFilter("jmason@ebi.ac.uk", "mouseinfo"));
		wt = client.target(endpoint);
	}


	/**
	 * returns the iMits representation of an es cell
	 *
	 * @param esCellName the es cell ID of interest
	 * @return a List of EsCellBean POJOs containing the information in iMits for all the passed in es cells matching the query criteria
	 */
	public List<EsCellBean> getEsCells(String esCellName) {
		LOG.debug("URI is: " + wt.path("targ_rep/es_cells.json").queryParam("name_eq", esCellName).getUri());

        if(! esCellCache.containsKey(esCellName)) {
            esCellCache.put(esCellName, wt
                    .path("targ_rep/es_cells.json")
                    .queryParam("name_eq", esCellName)
                    .request(MediaType.APPLICATION_JSON)
                    .get(new GenericType<List<EsCellBean>>() {
                    }));
        }
        return esCellCache.get(esCellName);
	}


	/**
	 * returns the iMits representation of the es cell passed in
	 *
	 * @param esCellName the es cell ID of interest
	 * @return an EsCellBean POJO containing the information in iMits for the passed in es cell
	 */
	public EsCellBean getEsCell(String esCellName) {
		return getEsCells(esCellName).get(0);
	}


	/**
	 * returns the iMits representation of the allele passed in
	 *
	 * @param alleleId the allele ID of interest
	 * @return an AlleleBean POJO containing the information in iMits for the passed in es cell
	 */
	public List<AlleleBean> getAlleles(String alleleId) {
		LOG.debug("URI is: " + wt.path("targ_rep/alleles.json").queryParam("id_eq", alleleId).getUri());

		return wt
			.path("targ_rep/alleles.json")
			.queryParam("id_eq", alleleId)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.get(new GenericType<List<AlleleBean>>() {
			});
	}


	public AlleleBean getAllele(String alleleId) {
		LOG.debug("URI is: " + wt.path("targ_rep/alleles.json").queryParam("id_eq", alleleId).getUri());

		return wt
			.path("targ_rep/alleles.json")
			.queryParam("id_eq", alleleId)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.get(new GenericType<List<AlleleBean>>() {
			})
			.get(0);
	}


	/**
	 * Returns a list of iMits micro injection attempts
	 *
	 * @param productionCenter the production center (WTSI, ICS, etc.)
	 * @return a list of micro injection attempts for this production center
	 */
	public List<MicroInjectionAttemptBean> getMicroInjectionAttempts(String productionCenter) {
		LOG.debug("URI is: " + wt.path("mi_attempts.json")
			.queryParam("production_centre_name_eq", productionCenter)
			.getUri());

		return wt
			.path("mi_attempts.json")
			.queryParam("production_centre_name_eq", productionCenter)
			.request(MediaType.APPLICATION_JSON)
			.get(new GenericType<List<MicroInjectionAttemptBean>>() {
			});
	}


	public List<MicroInjectionAttemptBean> getMiAttemptsByEsCell(String esCell) {
		LOG.debug("URI is: " + wt.path("mi_attempts.json")
			.queryParam("es_cell_name_eq", esCell).getUri());

		if( ! miAttemptByEsCellCache.containsKey(esCell)) {

            // Default to empty mi attempt
            miAttemptByEsCellCache.put(esCell, Arrays.asList(new MicroInjectionAttemptBean()));

            for (int tries = 1; tries <= IMITS_TRIES; tries++) {

                try {

                    // Override with a mi attempt from imits
                    miAttemptByEsCellCache.put(esCell, wt
                        .path("mi_attempts.json")
                        .queryParam("es_cell_name_eq", esCell)
                        .request(MediaType.APPLICATION_JSON)
                        .get(new GenericType<List<MicroInjectionAttemptBean>>() {
                        }));

                } catch (ServerErrorException | ClientErrorException e) {

                    // An error occurred talking to Imits.  Maybe wait
                    // a while and try again

                    try {

                        // Reset the client for good measure
                        this.initEndPoint(IMITS_URL, true);

                        if (e.getMessage().contains("HTTP 500 Internal Server Error")) {

                            LOG.warn(esCell + " Error communicating with iMITS: " + e.getMessage() + ". Permanent error. not trying again.\n" + "URI is: " + wt.path("mi_attempts.json").queryParam("es_cell_name_eq", esCell).getUri());
                            break;

                        } else {

                            LOG.warn("Attempt " + tries + " -- " + esCell + " Error communicating with iMITS: " + e.getMessage() + ". Waiting a while and trying again.\n" + "URI is: " + wt.path("mi_attempts.json").queryParam("es_cell_name_eq", esCell).getUri());
                            Thread.sleep(5000); // 5 seconds
                            LOG.error("Trying again: reset client.");

                        }
                    } catch (InterruptedException | KeyManagementException | NoSuchAlgorithmException e1) {
                        LOG.error("EXCEPTION:\n" + ExceptionUtils.getFullStackTrace(e1));
                    }
                }
            }
		}

		return miAttemptByEsCellCache.get(esCell);

	}


	public String getGeneByEscell(String esCell) {
		try {
			EsCellBean escell = getEsCell(esCell);
			AlleleBean allele = getAllele(escell.getAllele_id());
			return allele.getMarker_symbol();
		} catch (IndexOutOfBoundsException e) {
			LOG.debug("Cannot find gene for es cell " + esCell);
		}
		return null;
	}


	/**
	 * Get the gene accession id by passing a colony ID to imits and
	 * interrogating the phenotype attempt endpoint
	 *
	 * @param colonyId the colony ID to lookup in iMits
	 * @return the mgi id of the gene
	 */
//	public String getGeneByColonyId(String colonyId) {
//
//		PhenotypeAttemptBean phenotypeAttempt = new PhenotypeAttemptBean();
//
//		if (phenotypeAttemptCache.containsKey(colonyId)) {
//			phenotypeAttempt = phenotypeAttemptCache.get(colonyId);
//		} else {
//			LOG.info("URI is: " + wt.path("phenotype_attempts.json").queryParam("colony_name_eq", colonyId).getUri());
//
//			List<PhenotypeAttemptBean> phenotypeAttempts = wt
//				.path("phenotype_attempts.json")
//				.queryParam("colony_name_eq", colonyId)
//				.request(MediaType.APPLICATION_JSON_TYPE)
//				.get(new GenericType<List<PhenotypeAttemptBean>>() {
//				});
//
//			if (phenotypeAttempts != null && phenotypeAttempts.size() > 0) {
//				phenotypeAttempt = phenotypeAttempts.get(0);
//				phenotypeAttemptCache.put(colonyId, phenotypeAttempt);
//			}
//		}
//
//		return phenotypeAttempt.getMgi_accession_id();
//	}
    @Override
    public String getGeneByColonyId(String colonyId, String center) {

	    // Try phenotype_attempt first
	    PhenotypeAttemptBean phenotypeAttempt = new PhenotypeAttemptBean();

	    if (phenotypeAttemptCache.containsKey(colonyId)) {
		    phenotypeAttempt = phenotypeAttemptCache.get(colonyId);
	    } else {
		    LOG.debug("URI is: " + wt.path("phenotype_attempts.json").queryParam("phenotyping_productions_colony_name_eq", colonyId).getUri());

		    List<PhenotypeAttemptBean> phenotypeAttempts = wt
			    .path("phenotype_attempts.json")
			    .queryParam("phenotyping_productions_colony_name_eq", colonyId)
			    .request(MediaType.APPLICATION_JSON_TYPE)
			    .get(new GenericType<List<PhenotypeAttemptBean>>() {
			    });

		    if (phenotypeAttempts != null && phenotypeAttempts.size() > 0) {
			    for (PhenotypeAttemptBean pa : phenotypeAttempts) {
				    if(pa.getMgi_accession_id()!=null) {
					    phenotypeAttemptCache.put(colonyId, pa);
					    break;
				    }
			    }
		    }
	    }

	    // Use the gene from the MI attempt if it was found
	    if (phenotypeAttemptCache.containsKey(colonyId) && phenotypeAttemptCache.get(colonyId).getMgi_accession_id()!=null) {
		    return phenotypeAttemptCache.get(colonyId).getMgi_accession_id();
	    }

	    // If es cell by phenotype attempt doesn't exist, try MI attempt
	    if (! miAttemptByEsCellAndCenterCache.containsKey(colonyId)) {

            LOG.debug("URI is: " + wt.path("mi_attempts.json").queryParam("colony_name_eq", colonyId).getUri());

            List<MicroInjectionAttemptBean> miAttepts = wt
                    .path("mi_attempts.json")
                    .queryParam("colony_name_eq", colonyId)
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .get(new GenericType<List<MicroInjectionAttemptBean>>() {
                    });

            for (MicroInjectionAttemptBean miAttempt : miAttepts) {
                if (miAttempt.getProduction_centre_name().toUpperCase().equals(center.toUpperCase())) {
                    miAttemptByEsCellAndCenterCache.put(colonyId, miAttempt);
                    break;
                }
            }
            if (! miAttemptByEsCellAndCenterCache.containsKey(colonyId) ) {
                miAttemptByEsCellAndCenterCache.put(colonyId, new MicroInjectionAttemptBean());
            }
        }

        // Use the gene from the MI attempt if it was found
        if (miAttemptByEsCellAndCenterCache.containsKey(colonyId) && miAttemptByEsCellAndCenterCache.get(colonyId).getMgi_accession_id()!=null) {
            return miAttemptByEsCellAndCenterCache.get(colonyId).getMgi_accession_id();
        }

	    return null;

    }


	/**
	 * Get the allele symbol by passing a colony ID to iMits and
	 * interrogating the phenotype attempt endpoint
	 *
	 * @param colonyId the colony ID to lookup in iMits
	 * @return the mgi id of the gene
	 */
	@Override
	public String getAlleleSymbolByColonyId(String colonyId) {

		String alleleSymbol = null;
		PhenotypeAttemptBean phenotypeAttempt = null;

		if (phenotypeAttemptCache.containsKey(colonyId)) {

			phenotypeAttempt = phenotypeAttemptCache.get(colonyId);

		} else {
			LOG.debug("URI is: " + wt.path("phenotype_attempts.json").queryParam("phenotyping_productions_colony_name_eq", colonyId).getUri());

			try {

				for (int tries = 1; tries <= IMITS_TRIES; tries++) {

					try {

						List<PhenotypeAttemptBean> phenotypeAttempts = wt
							.path("phenotype_attempts.json")
							.queryParam("phenotyping_productions_colony_name_eq", colonyId)
							.request(MediaType.APPLICATION_JSON_TYPE)
							.get(new GenericType<List<PhenotypeAttemptBean>>() {});

						if (phenotypeAttempts != null && phenotypeAttempts.size() > 0) {

							phenotypeAttempt = phenotypeAttempts.get(0);
							phenotypeAttemptCache.put(colonyId, phenotypeAttempt);

						} else {
							LOG.debug("Imits communication successful. ColonyID: " + colonyId + " could not find iMITS phenotype attempt.\n" +
								"  URI is: " + wt.path("phenotype_attempts.json").queryParam("phenotyping_productions_colony_name_eq", colonyId).getUri());

							phenotypeAttemptCache.put(colonyId, new PhenotypeAttemptBean());

						}

						// If we make it here, we've been successful at talking with iMits.  Stop iterating
						tries=IMITS_TRIES+1;
						break;

					} catch (ServerErrorException | ClientErrorException e) {

						// An error occurred talking to Imits.  Maybe wait
						// a while and try again

						try {

							// Reset the client for good measure
							this.initEndPoint(IMITS_URL, true);

							if (e.getMessage().contains("HTTP 500 Internal Server Error")) {

								LOG.warn(colonyId + " Error communicating with iMITS: " + e.getMessage() + ". Permanent error. not trying again.\n" + "URI is: " + wt.path("phenotype_attempts.json").queryParam("phenotyping_productions_colony_name_eq", colonyId).getUri());
								break;

							} else {

								LOG.warn("Attempt " + tries + " -- " + colonyId + " Error communicating with iMITS: " + e.getMessage() + ". Waiting a while and trying again.\n" + "URI is: " + wt.path("phenotype_attempts.json").queryParam("phenotyping_productions_colony_name_eq", colonyId).getUri());
								Thread.sleep(5000); // 5 seconds
								LOG.error("Trying again: reset client.");

							}
						} catch (InterruptedException | KeyManagementException | NoSuchAlgorithmException e1) {
							LOG.error("EXCEPTION:\n" + ExceptionUtils.getFullStackTrace(e1));
						}
					}
				}

				// if we couldn't find the phenotyping attempt in Imits,
				// lets cache an empty one so as to not attempt again for
				// this colony
				if (!phenotypeAttemptCache.containsKey(colonyId)) {
					phenotypeAttemptCache.put(colonyId, new PhenotypeAttemptBean());
				}

			} catch (IndexOutOfBoundsException e) {
				LOG.info("Cannot find allele for colonyId: " + colonyId);
			}
		}

		// We found (or didn't find) the phenotype attempt, try to get the allele symbol
		if (phenotypeAttempt != null && phenotypeAttempt.getMouse_allele_symbol() != null) {

			// 2015-06-17 per PM: always use the mouse_allele_symbol field
			alleleSymbol = phenotypeAttempt
				.getMouse_allele_symbol()
				.replaceAll("<sup>", "<")
				.replaceAll("</sup>", ">");
		}

		return alleleSymbol;
	}


	/**
	 * Get the allele accession id by passing an es cell ID to imits and
	 * interrogating the targ_rep
	 *
	 * @param esCell es cell identifier
	 * @return the mgi id of the gene
	 */
	public String getAlleleMGIIDByEscell(String esCell) {
		try {
			for (int tries = 1; tries <= IMITS_TRIES; tries++) {
				try {

					EsCellBean escell = getEsCell(esCell);
					return escell.getMgi_allele_id();

				} catch (ServerErrorException e) {

					// Imits timed out.  Wait a while and try again

					try {

						// Reset the client for good measure
						this.initEndPoint(IMITS_URL, true);

						if (e.getMessage().contains("HTTP 500 Internal Server Error")) {

							LOG.warn(esCell + " Error communicating with iMITS: " + e.getMessage() + ". Permanent error. not trying again.\n URI is: " + wt.path("targ_rep/es_cells.json").queryParam("name_eq", esCell).getUri());
							break;

						} else {

							LOG.warn("Attempt " + tries + " -- " + esCell + " Error communicating with iMITS: " + e.getMessage() + ". Waiting a while and trying again.\n URI is: " + wt.path("targ_rep/es_cells.json").queryParam("name_eq", esCell).getUri());
							Thread.sleep(5000); // 5 seconds
							LOG.error("Trying again: reset client.");

						}

					} catch (InterruptedException e1) {
						// if any other thread interrupts this one
						LOG.error("Sleep was interrupted: " + e1.getMessage() + ".  Continuing normal operation.");
					} catch (NoSuchAlgorithmException | KeyManagementException e1) {
						e1.printStackTrace();
					}
				}
			} // end for (int tries=0; tries<5; tries++)
		} catch (IndexOutOfBoundsException e) {
			LOG.info("Cannot find allele for ESCELL " + esCell);
		}

		// Couldn't find the allele
		return null;
	}


	/**
	 * Get the strain of the colony id by interrogating the phenotype attempts endpoint of iMits
	 *
	 * @param colonyId the colony ID to lookup in iMits
	 * @return the mgi id of the strain
	 */
	@Override
	public String getStrainByColonyId(String colonyId, String center) {

        if (! miAttemptByEsCellAndCenterCache.containsKey(colonyId)) {

            LOG.debug("URI is: " + wt.path("mi_attempts.json").queryParam("colony_name_eq", colonyId).getUri());

	        MicroInjectionAttemptBean miAttempt = getMiAttemptByColonyIdCenter(colonyId, center);

	        if (miAttempt == null) {
		        miAttemptByEsCellAndCenterCache.put(colonyId, new MicroInjectionAttemptBean());
            }
        }


		// IF cant get strain by mi attempt
		PhenotypeAttemptBean phenotypeAttempt = new PhenotypeAttemptBean();

		if (phenotypeAttemptCache.containsKey(colonyId)) {

			phenotypeAttempt = phenotypeAttemptCache.get(colonyId);

		} else {

			phenotypeAttempt = getPhenotypeAttemptBean(colonyId, phenotypeAttempt);
		}

		String strainName = phenotypeAttempt.getColony_background_strain_mgi_accession();

		if (strainName.isEmpty()) {
			strainName = phenotypeAttempt.getColony_background_strain_name();
		}

		return strainName;
	}

	private MicroInjectionAttemptBean getMiAttemptByColonyIdCenter(String colonyId, String center) {

		if (miAttemptByEsCellAndCenterCache.containsKey(colonyId)) {
			return miAttemptByEsCellAndCenterCache.get(colonyId);
		}

		List<MicroInjectionAttemptBean> miAttempts;
		for (int tries = 1; tries <= IMITS_TRIES; tries++) {

			try {
				miAttempts = wt.path("mi_attempts.json").queryParam("colony_name_eq", colonyId).request(MediaType.APPLICATION_JSON_TYPE).get(new GenericType<List<MicroInjectionAttemptBean>>() {
				});

				if (miAttempts != null && miAttempts.size() > 0) {

					for (MicroInjectionAttemptBean miAttempt : miAttempts) {
						if (miAttempt.getProduction_centre_name().toUpperCase().equals(center.toUpperCase())) {
							miAttemptByEsCellAndCenterCache.put(colonyId, miAttempt);
						}
					}

					// Found it, break out of the try loop
					break;
				} else {
					LOG.debug("Imits communication successful. ColonyID: " + colonyId + " could not find iMITS mi attempts.\n" +
						"  URI is: " + wt.path("mi_attempts.json").queryParam("colony_name_eq", colonyId).getUri());

					// store an empty phenotype attempt in the cache
					miAttemptByEsCellAndCenterCache.put(colonyId, new MicroInjectionAttemptBean());

					break;
				}

			} catch (ServerErrorException | ClientErrorException e) {
				// Imits timed out.  Wait a while and try again

				try {


					// Reset the client for good measure
					this.initEndPoint(IMITS_URL, true);

					if (e.getMessage().contains("HTTP 500 Internal Server Error")) {

						LOG.warn(colonyId + " Error communicating with iMITS: " + e.getMessage() + ". Permanent error. not trying again.\n" + "URI is: " + wt.path("phenotype_attempts.json").queryParam("colony_name_eq", colonyId).getUri());
						break;

					} else {

						LOG.warn("Attempt " + tries + " -- " + colonyId + " Error communicating with iMITS: " + e.getMessage() + ". Waiting a while and trying again.\n" + "URI is: " + wt.path("phenotype_attempts.json").queryParam("colony_name_eq", colonyId).getUri());
						Thread.sleep(5000); // 5 seconds
						LOG.error("Trying again: reset client.");

					}

				} catch (KeyManagementException | NoSuchAlgorithmException | InterruptedException e1) {
					LOG.error("EXCEPTION:\n" + ExceptionUtils.getFullStackTrace(e1));
				}

			}
		}

		if (miAttemptByEsCellAndCenterCache.containsKey(colonyId)) {
			return miAttemptByEsCellAndCenterCache.get(colonyId);
		}

		return null;
	}

	/**
	 * Get the strain of the colony id by interrogating the phenotype attempts endpoint of iMits
	 *
	 * @param colonyId the colony ID to lookup in iMits
	 * @return the mgi id of the strain
	 */
	@Override
	public String getStrainNameByColonyId(String colonyId, String center) {


		// IF cant get strain by mi attempt
		PhenotypeAttemptBean phenotypeAttempt = new PhenotypeAttemptBean();

		if (phenotypeAttemptCache.containsKey(colonyId)) {

			phenotypeAttempt = phenotypeAttemptCache.get(colonyId);

		} else {

			phenotypeAttempt = getPhenotypeAttemptBean(colonyId, phenotypeAttempt);
		}

		String strainName = phenotypeAttempt.getColony_background_strain_name();

		if (strainName == null) {

			// Colony background strain not found for this phenotype attempt (or phenotype attempt not found)
			//
			// Per pm9, if the cre_excision_required field is false and the background strain
			// is empty, substitute the MI Attempt strain for this colony

			if (phenotypeAttempt.getCre_excision_required() == null || phenotypeAttempt.getCre_excision_required().equals("false")) {

				if (phenotypeAttempt.getMi_attempt_colony_name() != null) {
					String miColonyName = phenotypeAttempt.getMi_attempt_colony_name();
					MicroInjectionAttemptBean miAttempt = getMiAttemptByColonyIdCenter(miColonyName, center);

					// Use the gene acc from the MI attempt if it was found
					//					if (miAttemptByEsCellAndCenterCache.containsKey(miColonyName) && miAttemptByEsCellAndCenterCache.get(miColonyName).getColony_background_strain_mgi_accession() != null) {
					//						strainName = miAttemptByEsCellAndCenterCache.get(miColonyName).getColony_background_strain_mgi_accession();
					//					}

					// Use the gene symbol from the MI attempt if it was found
					if (strainName == null && miAttemptByEsCellAndCenterCache.containsKey(miColonyName) && miAttemptByEsCellAndCenterCache.get(miColonyName).getColony_background_strain_name() != null) {
						strainName = miAttemptByEsCellAndCenterCache.get(miColonyName).getColony_background_strain_name();
					}

				}
			}
		}

		return strainName;
	}

	private PhenotypeAttemptBean getPhenotypeAttemptBean(String colonyId, PhenotypeAttemptBean phenotypeAttempt) {
		LOG.debug("URI is: " + wt.path("phenotype_attempts.json").queryParam("colony_name_eq", colonyId).getUri());

		try {
			for (int tries = 1; tries <= IMITS_TRIES; tries++) {
				try {

					List<PhenotypeAttemptBean> phenotypeAttempts = wt.path("phenotype_attempts.json").queryParam("phenotyping_productions_colony_name_eq", colonyId).request(MediaType.APPLICATION_JSON_TYPE).get(new GenericType<List<PhenotypeAttemptBean>>() {
					});

					if (phenotypeAttempts != null && phenotypeAttempts.size() > 0) {
						phenotypeAttempt = phenotypeAttempts.get(0);

						LOG.info("Found phenotyping attempt for ColonyID {}", colonyId);

						phenotypeAttemptCache.put(colonyId, phenotypeAttempt);

						// Found it, break out of the try loop
						break;
					} else {
						LOG.debug("Imits communication successful. ColonyID: " + colonyId + " could not find iMITS phenotype attempt.\n" +
							"  URI is: " + wt.path("phenotype_attempts.json").queryParam("phenotyping_productions_colony_name_eq", colonyId).getUri());

						// store an empty phenotype attempt in the cache
						phenotypeAttemptCache.put(colonyId, new PhenotypeAttemptBean());

						break;
					}
				} catch (ServerErrorException | ClientErrorException e) {
					// Imits timed out.  Wait a while and try again

					try {


						// Reset the client for good measure
						this.initEndPoint(IMITS_URL, true);

						if (e.getMessage().contains("HTTP 500 Internal Server Error")) {

							LOG.warn(colonyId + " Error communicating with iMITS: " + e.getMessage() + ". Permanent error. not trying again.\n" + "URI is: " + wt.path("phenotype_attempts.json").queryParam("phenotyping_productions_colony_name_eq", colonyId).getUri());
							break;

						} else {

							LOG.warn("Attempt " + tries + " -- " + colonyId + " Error communicating with iMITS: " + e.getMessage() + ". Waiting a while and trying again.\n" + "URI is: " + wt.path("phenotype_attempts.json").queryParam("phenotyping_productions_colony_name_eq", colonyId).getUri());
							Thread.sleep(5000); // 5 seconds
							LOG.error("Trying again: reset client.");

						}

					} catch (KeyManagementException | NoSuchAlgorithmException | InterruptedException e1) {
						LOG.error("EXCEPTION:\n" + ExceptionUtils.getFullStackTrace(e1));
					}
				}
			}
		} catch (IndexOutOfBoundsException e) {
			LOG.error("Cannot find strain for colonyId: " + colonyId);
		}
		return phenotypeAttempt;
	}

	@Override
	public String getColonyIdByEsCell(String specimenID, String center) {
		List<MicroInjectionAttemptBean> miAttempts = getMiAttemptsByEsCell(specimenID);
		String colonyId = null;

		for (MicroInjectionAttemptBean miAttempt : miAttempts) {

			// We do not want to map the later attempts, only the legacy attempts
            if(center != null && miAttempt!= null && miAttempt.getProduction_centre_name() != null && miAttempt.getConsortium_name() != null) {
			    if(center.toUpperCase().equals(miAttempt.getProduction_centre_name().toUpperCase()) && ! miAttempt.getConsortium_name().equals("MGP")) {
				    colonyId = miAttempt.getColony_name();
				    break;
			    }
            } else {
                LOG.warn("Missing data. Center {}, mi_center {}", center, miAttempt != null ? miAttempt.getProduction_centre_name() : null);
            }
		}

		return colonyId;
	}

	@Override
	public String getProjectByEsCell(String specimenID, String center) {
		List<MicroInjectionAttemptBean> miAttempts = getMiAttemptsByEsCell(specimenID);
		String project = null;

		for (MicroInjectionAttemptBean miAttempt : miAttempts) {

			// We do not want to map the later attempts, only the legacy attempts
            if(center != null && miAttempt!= null && miAttempt.getProduction_centre_name() != null && miAttempt.getConsortium_name() != null) {
                if(center.toUpperCase().equals(miAttempt.getProduction_centre_name().toUpperCase()) && ! miAttempt.getConsortium_name().equals("MGP")) {
                    project = miAttempt.getConsortium_name();
                    break;
                }
            } else {
                LOG.warn("Missing data. Center {}, mi_center {}", center, miAttempt != null ? miAttempt.getProduction_centre_name() : null);
            }
		}

		return project;
	}



    /**
     * Make sure to release resources
     */
    public void finalize() {
        try {
            super.finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        if (client != null) {
            client.close();
        }
    }

    /**
     * get an ssl manager.
     * <p/>
     * NOTE: this is inherently insecure as it relies on a trust manager that
     * is inherently insecure.
     *
     * @return an SSLContext for use in verifying SSL communication
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    private SSLContext getSecurityManager() throws NoSuchAlgorithmException, KeyManagementException {

        SSLContext sslContext;
        sslContext = SSLContext.getInstance("TLS");
        sslContext.init(new KeyManager[0],
                new TrustManager[]{new DefaultTrustManager()},
                new SecureRandom());
        SSLContext.setDefault(sslContext);
        return sslContext;
    }



    /**
	 * internal trust manager class for validating an SSL certificate.
	 * <p/>
	 * NOTE: This trust manager is inherently insecure as it validates all certificates
	 */
	public class DefaultTrustManager implements X509TrustManager {

		@Override
		public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		}


		@Override
		public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		}


		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}


}
