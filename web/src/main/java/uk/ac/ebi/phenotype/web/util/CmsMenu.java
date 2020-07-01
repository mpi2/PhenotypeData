/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/
package uk.ac.ebi.phenotype.web.util;


import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.cda.utilities.HttpProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class CmsMenu extends HttpProxy {

	private static final Logger log = LoggerFactory.getLogger(CmsMenu.class);

	private static String publicMenu = null;

	private static final String DEFAULT_MENU = "[{\"id\":16,\"name\":\"About the IMPC\",\"classes\":\"about-impc\",\"link\":\"https:\\/\\/www.mousephenotype.org\\/about-impc\\/\",\"children\":[{\"name\":\"Consortium Members\",\"link\":\"https:\\/\\/www.mousephenotype.org\\/about-impc\\/consortium-members\\/\",\"sort\":1,\"children\":[]},{\"name\":\"Collaborations\",\"link\":\"https:\\/\\/www.mousephenotype.org\\/about-impc\\/collaborations\\/\",\"sort\":2,\"children\":[]},{\"name\":\"Funding\",\"link\":\"https:\\/\\/www.mousephenotype.org\\/about-impc\\/funding\\/\",\"sort\":3,\"children\":[]},{\"name\":\"Animal Welfare\",\"link\":\"https:\\/\\/www.mousephenotype.org\\/about-impc\\/animal-welfare\\/\",\"sort\":4,\"children\":[]},{\"name\":\"About KOMP\",\"link\":\"https:\\/\\/www.mousephenotype.org\\/about-impc\\/about-komp\\/\",\"sort\":5,\"children\":[]},{\"name\":\"About IKMC\",\"link\":\"https:\\/\\/www.mousephenotype.org\\/about-impc\\/about-ikmc\\/\",\"sort\":6,\"children\":[]}]},{\"id\":2451,\"name\":\"Data\",\"classes\":\"data\",\"link\":\"https:\\/\\/www.mousephenotype.org\\/understand\\/\",\"children\":[{\"name\":\"Data Focus\",\"link\":\"https:\\/\\/www.mousephenotype.org\\/understand\\/data-focus\\/\",\"sort\":3,\"children\":[{\"name\":\"Embryo Vignettes\",\"link\":\"https:\\/\\/www.mousephenotype.org\\/understand\\/data-focus\\/embryo-vignettes\\/\"},{\"name\":\"Embryo Development\",\"link\":\"https:\\/\\/www.mousephenotype.org\\/understand\\/data-focus\\/embryo-development\\/\"},{\"name\":\"Cardiovascular\",\"link\":\"https:\\/\\/www.mousephenotype.org\\/understand\\/data-focus\\/cardiovascular\\/\"},{\"name\":\"Pain\",\"link\":\"https:\\/\\/www.mousephenotype.org\\/understand\\/data-focus\\/pain\\/\"},{\"name\":\"Histopathology\",\"link\":\"https:\\/\\/www.mousephenotype.org\\/understand\\/data-focus\\/histopathology\\/\"},{\"name\":\"Embryo Heatmap\",\"link\":\"https:\\/\\/www.mousephenotype.org\\/understand\\/data-focus\\/embryo-heatmap\\/\"}]},{\"name\":\"Accessing the Data\",\"link\":\"https:\\/\\/www.mousephenotype.org\\/understand\\/accessing-the-data\\/\",\"sort\":1,\"children\":[{\"name\":\"Latest data release\",\"link\":\"https:\\/\\/www.mousephenotype.org\\/understand\\/accessing-the-data\\/latest-data-release\\/\"},{\"name\":\"Access via API\",\"link\":\"https:\\/\\/www.mousephenotype.org\\/understand\\/accessing-the-data\\/access-via-api\\/\"},{\"name\":\"Access via FTP\",\"link\":\"https:\\/\\/www.mousephenotype.org\\/understand\\/accessing-the-data\\/access-via-ftp\\/\"},{\"name\":\"Batch query\",\"link\":\"https:\\/\\/www.mousephenotype.org\\/understand\\/accessing-the-data\\/batch-query\\/\"}]},{\"name\":\"Understanding Our Data\",\"link\":\"https:\\/\\/www.mousephenotype.org\\/understand\\/the-data\\/\",\"sort\":0,\"children\":[{\"name\":\"How we generate our data\",\"link\":\"https:\\/\\/www.mousephenotype.org\\/understand\\/the-data\\/how-we-generate-the-data\\/\"},{\"name\":\"Phenotyping process\",\"link\":\"https:\\/\\/www.mousephenotype.org\\/understand\\/the-data\\/phenotyping-process-impress\\/\"},{\"name\":\"Allele design\",\"link\":\"https:\\/\\/www.mousephenotype.org\\/understand\\/the-data\\/allele-design\\/\"}]},{\"name\":\"Advanced Tools\",\"link\":\"https:\\/\\/www.mousephenotype.org\\/understand\\/advanced-tools\\/\",\"sort\":2,\"children\":[{\"name\":\"IMPReSS\",\"link\":\"https:\\/\\/www.mousephenotype.org\\/understand\\/advanced-tools\\/impress\\/\"},{\"name\":\"Parallel Coordinates\",\"link\":\"https:\\/\\/www.mousephenotype.org\\/understand\\/advanced-tools\\/parallel-coordinates\\/\"},{\"name\":\"PhenStat\",\"link\":\"https:\\/\\/www.mousephenotype.org\\/understand\\/advanced-tools\\/phenstat\\/\"},{\"name\":\"iMits\",\"link\":\"https:\\/\\/www.mousephenotype.org\\/understand\\/advanced-tools\\/imits\\/\"},{\"name\":\"PhenoDCC Tools\",\"link\":\"https:\\/\\/www.mousephenotype.org\\/understand\\/advanced-tools\\/phenodcc-homepage\\/\"},{\"name\":\"Embryo viewer\",\"link\":\"https:\\/\\/www.mousephenotype.org\\/understand\\/advanced-tools\\/embryo-viewer\\/\"}]}]},{\"id\":18,\"name\":\"Human Diseases\",\"classes\":\"human-diseases\",\"link\":\"https:\\/\\/www.mousephenotype.org\\/human-diseases\\/\",\"children\":[]},{\"id\":7486,\"name\":\"Publications\",\"classes\":\"publications\",\"link\":\"https:\\/\\/www.mousephenotype.org\\/publications\\/\",\"children\":[{\"name\":\"Papers using IMPC resources\",\"link\":\"https:\\/\\/www.mousephenotype.org\\/publications\\/papers-using-impc-resources\\/\",\"sort\":2,\"children\":[]},{\"name\":\"Latest IMPC Papers\",\"link\":\"https:\\/\\/www.mousephenotype.org\\/publications\\/latest-impc-papers\\/\",\"sort\":0,\"children\":[]},{\"name\":\"Data supporting IMPC papers\",\"link\":\"https:\\/\\/www.mousephenotype.org\\/publications\\/data-supporting-impc-papers\\/\",\"sort\":1,\"children\":[{\"name\":\"Essential Genes - Translating to Other Species\",\"link\":\"https:\\/\\/www.mousephenotype.org\\/publications\\/data-supporting-impc-papers\\/essential-genes-linking-to-disease\\/\"},{\"name\":\"Essential Genes \\u2013 Linking to Disease\",\"link\":\"https:\\/\\/www.mousephenotype.org\\/publications\\/data-supporting-impc-papers\\/essential-genes-linking-to-disease-2\\/\"},{\"name\":\"Genetic Basis for Metabolic Diseases\",\"link\":\"https:\\/\\/www.mousephenotype.org\\/publications\\/data-supporting-impc-papers\\/metabolism\\/\"},{\"name\":\"Genes Critical for Hearing Identified\",\"link\":\"https:\\/\\/www.mousephenotype.org\\/publications\\/data-supporting-impc-papers\\/hearing\\/\"},{\"name\":\"Sexual dimorphism\",\"link\":\"https:\\/\\/www.mousephenotype.org\\/publications\\/data-supporting-impc-papers\\/sexual-dimorphism\\/\"}]}]},{\"id\":19,\"name\":\"News\",\"classes\":\"news\",\"link\":\"https:\\/\\/www.mousephenotype.org\\/news\\/\",\"children\":[]},{\"id\":983,\"name\":\"Blog\",\"classes\":\"blog\",\"link\":\"https:\\/\\/www.mousephenotype.org\\/blog\\/\",\"children\":[]}]";

	/**
	 * Flush the cache every so often.  This is triggered by Spring on a schedule of
	 * fixedDelay = 10 * 60 * 1000 ,  initialDelay = 500
	 */
	@CacheEvict(allEntries = true, value = "menu")
	@Scheduled(fixedDelay = 1000 * 5 ,  initialDelay = 500)
	public void resetMenu() {
		System.out.println("Clearing menu cache");
		publicMenu = null;
	}

	/**
	 * Get the current menus from the CMS system -- both the main menu and the footer
	 *
	 * @return a JSON representation of the CMS menu
	 */
	@Cacheable(sync = true, value = "menu")
	public List<MenuItem> getCmsMenu(String cmsBaseUrl, String baseUrl) {

		String content = publicMenu;
		Random randomGenerator = new Random();

		try {

			if (publicMenu == null || randomGenerator.nextInt(10000) == 1) {
				log.debug("Menu expired or first load, attempting to get CMS menu.");

				// Default is to add the protocol to the url if it's missing
				URL url = new URL("https:" + cmsBaseUrl + "/jsonmenu");
				if (cmsBaseUrl.contains("http")) {
					url = new URL(cmsBaseUrl + "/jsonmenu");
				}

				publicMenu = this.getContent(url).replaceAll("\\\\", "");
				content = publicMenu;
			}

			if (StringUtils.isEmpty(content)) {
				throw new Exception("Error retrieving CMS menu");
			}

		} catch (Exception e) {
			// If we can't get the menu, default to the logged out menu
			log.error("Cannot retrieve menu from CMS. Using default menu.", e);
			publicMenu = DEFAULT_MENU;
			content = publicMenu;
		}

		// Replace all hardcoded hostnames to the name of the current environment
		content = content.replaceAll("https:\\\\/\\\\/www.mousephenotype.org", cmsBaseUrl);

		// Add the "My Genes" link to the menu
        content = prependToMenu(content, baseUrl);

        try {
			final JSONArray jsonMenu = new JSONArray(content);
			return getMenu(jsonMenu);
		} catch (JSONException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}

	}



	String prependToMenu(String menu, String baseUrl) {

		String summaryMenu = "{\"name\":\"My Genes\",\"link\":\"" + baseUrl + "/summary\",\"children\":[]},";
		int idx = menu.indexOf("{");
		StringBuilder sb = new StringBuilder(menu);
		sb.insert(idx, summaryMenu);

		return sb.toString();
	}


	/**
	 * Specific transformer for the CMS menu from JSON representation of the menu
	 * to a list of MenuItems of each top level and it's children
	 *
	 * @param menu JSONArray of menu items
	 * @return List of MenuItems representing the menu
	 * @throws JSONException when JSON is poorly formatted or otherwise incorrect
	 */
	public List<MenuItem> getMenu (JSONArray menu) throws JSONException {
		List<MenuItem> menuItems = new ArrayList<>();

		for (int i = 0; i < menu.length(); i++) {
			JSONObject topLevel = (JSONObject) menu.get(i);
			String     classes  = topLevel.has("classes") ? topLevel.getString("classes") : null;
			MenuItem   mi       = new MenuItem(topLevel.getString("link"), topLevel.getString("name"), classes);
			assignSort(topLevel, mi);

			JSONArray children = topLevel.getJSONArray("children");
			for (int j = 0; j < children.length(); j++) {
				JSONObject child = (JSONObject) children.get(j);
				MenuItem childMi = new MenuItem(child.getString("link"), child.getString("name"));
				assignSort(child, childMi);

				JSONArray grandChildren = child.getJSONArray("children");
				for (int k = 0; k < grandChildren.length(); k++) {
					JSONObject grandChild = (JSONObject) grandChildren.get(k);
					MenuItem grandChildMi = new MenuItem(grandChild.getString("link"), grandChild.getString("name"));
					assignSort(grandChild, grandChildMi);

					childMi.addChild(grandChildMi);
				}
				if (childMi.getChildren() != null) {
					childMi.setChildren(
							childMi.getChildren().stream()
								.sorted(Comparator.comparing(MenuItem::getSort, Comparator.nullsLast(Comparator.naturalOrder())))
								.collect(Collectors.toList()));
				}

				mi.addChild(childMi);
			}
			if (mi.getChildren() != null) {
				mi.setChildren(
						mi.getChildren().stream()
								.sorted(Comparator.comparing(MenuItem::getSort, Comparator.nullsLast(Comparator.naturalOrder())))
								.collect(Collectors.toList()));
			}

			menuItems.add(mi);
		}

		return menuItems;
	}

	private void assignSort(JSONObject node, MenuItem menuItem) {
		try {
			if (node.has("sort")) {
				menuItem.setSort(node.getInt("sort"));
			}
		} catch (Exception ignored) { }
	}


	/**
	 * Menu ites include specific wordpress classes and identifiers that need to go along with the menu
	 * items in order to render correctly with the CSS.
	 */
	public class MenuItem {
		String url;
		String name;
		String cssId;
		String classes;
		String mobileCssId;
		String menuId;
		Integer sort;
		List<MenuItem> children;

		public MenuItem(String url, String name) {
			this.url = url;
			this.name = name;
		}

		public MenuItem(String url, String name, String classes) {
			this.url = url;
			this.name = name;
			this.classes = classes;
		}


		public void addChild(MenuItem menuItem) {
			if (children == null) { children = new ArrayList<>(); }
			this.children.add(menuItem);
		}

		public void setChildren(List<MenuItem> children) {
			this.children = children;
		}

		public String getUrl() {
			return url;
		}

		public String getName() {
			return name;
		}

		public Integer getSort() {
			return sort;
		}

		public void setSort(Integer sort) {
			this.sort = sort;
		}

		public String getCssId() {

			if (classes != null) {
				return classes;
			}

			String cssId = "";

			if (name.toLowerCase().contains("about")) {
				cssId = "menu-item-16";
			} else if (name.toLowerCase().contains("data")) {
				cssId = "menu-item-17";
			} else if (name.toLowerCase().contains("disease")) {
				cssId = "menu-item-18";
			} else if (name.toLowerCase().contains("news")) {
				cssId = "menu-item-19";
			} else if (name.toLowerCase().contains("publications")) {
				cssId = "menu-item-7486";
			} else if (name.toLowerCase().contains("blog")) {
				cssId = "menu-item-983";
			}

			return cssId;
		}

		public String getMobileCssId() {

			if (classes != null) {
				return classes;
			}

			String cssId = "";

			if (name.toLowerCase().contains("about")) {
				cssId = "object-id-7";
			} else if (name.toLowerCase().contains("data")) {
				cssId = "object-id-8";
			} else if (name.toLowerCase().contains("disease")) {
				cssId = "object-id-9";
			} else if (name.toLowerCase().contains("news")) {
				cssId = "object-id-10";
			} else if (name.toLowerCase().contains("blog")) {
				cssId = "object-id-978";
			}

			return cssId;
		}

		public String getMenuId() {

			String menuId = "";

			if (name.toLowerCase().contains("about")) {
				menuId = "about-menu";
			} else if (name.toLowerCase().contains("data")) {
				menuId = "data-menu";
			} else if (name.toLowerCase().contains("news")) {
				menuId = "news-menu";
			} else if (name.toLowerCase().contains("publications")) {
				menuId = "publications-menu";
			}

			return menuId;
		}

		public List<MenuItem> getChildren() {
			return children;
		}

		@Override
		public String toString() {
			return "MenuItem{" +
					"url='" + url + '\'' +
					", name='" + name + '\'' +
					", cssId='" + cssId + '\'' +
					", mobileCssId='" + mobileCssId + '\'' +
					", menuId='" + menuId + '\'' +
					", sort=" + sort +
					'}';
		}
	}
}