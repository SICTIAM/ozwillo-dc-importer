package org.ozwillo.dcimporter.service;

import java.util.Arrays;

import org.oasis_eu.spring.datacore.DatacoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SynchronizerService implements CommandLineRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(SynchronizerService.class);

	private final SystemUserService systemUserService;
	private final DatacoreClient datacoreClient;
	private final PublikService publikService;

	@Value("${publik.datacore.project}")
	private String datacoreProject;
	@Value("${publik.datacore.modelEM}")
	private String datacoreModelEM;
	@Value("${publik.datacore.modelSVE}")
	private String datacoreModelSVE;

	@Autowired
	public SynchronizerService(SystemUserService systemUserService, DatacoreClient datacoreClient,
	        PublikService publikService) {
		this.systemUserService = systemUserService;
		this.datacoreClient = datacoreClient;
		this.publikService = publikService;
	}

	public void run(String... args) {
		systemUserService.runAs(() -> {

			Arrays.asList(datacoreModelEM, datacoreModelSVE).forEach(type -> {
				if (datacoreClient.findResources(datacoreProject, type).isEmpty())
					try {
						publikService.syncPublikForms(type);
						LOGGER.debug("Requests successfully synchronized");
					} catch (Exception e) {
						LOGGER.debug("Unable to synchronize past requests", e);
					}
				else
					LOGGER.info("Requests are already synchronized");
			});
		});
	}
}