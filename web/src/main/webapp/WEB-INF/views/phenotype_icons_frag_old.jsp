<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<div>
	<div class="abnormalities">
		<div class="allicons"></div>
	
		<div class="no-sprite sprite_embryo_phenotype" data-hasqtip="27"
			title="${gene.markerSymbol} embryo phenotype measurements"></div>
		<div class="no-sprite sprite_reproductive_system_phenotype"
			data-hasqtip="27" title="${gene.markerSymbol} reproductive system phenotype measurements"></div>
		<div class="no-sprite sprite_mortality_aging" data-hasqtip="27"
			title="${gene.markerSymbol} mortality/aging"></div>
		<div class="no-sprite sprite_growth_size_body_region_phenotype"
			data-hasqtip="27" title="${gene.markerSymbol} growth/size/body region phenotype measurements"></div>
		<div class="no-sprite sprite_homeostasis_metabolism_phenotype_or_adipose_tissue_phenotype"
			data-hasqtip="27" title="${gene.markerSymbol} homeostasis/metabolism phenotype or adipose tissue phenotype measurements"></div>
		<div class="no-sprite sprite_behavior_neurological_phenotype_or_nervous_system_phenotype"
			data-hasqtip="27"
			title="${gene.markerSymbol} behavior/neurological phenotype or nervous system phenotype measurements"></div>
		<div class="no-sprite sprite_cardiovascular_system_phenotype"
			data-hasqtip="27" title="${gene.markerSymbol} cardiovascular system phenotype measurements"></div>
		<div class="no-sprite sprite_respiratory_system_phenotype"
			data-hasqtip="27" title="${gene.markerSymbol} respiratory system phenotype measurements"></div>
		<div class="no-sprite sprite_digestive_alimentary_phenotype_or_liver_biliary_system_phenotype"
			data-hasqtip="27"
			title="${gene.markerSymbol} digestive/alimentary phenotype or liver/biliary system phenotype measurements"></div>
		<div class="no-sprite sprite_renal_urinary_system_phenotype"
			data-hasqtip="27" title="${gene.markerSymbol} renal/urinary system phenotype measurements"></div>
	
		<div class="no-sprite sprite_limbs_digits_tail_phenotype"
			data-hasqtip="27" title="${gene.markerSymbol} limbs/digits/tail phenotype measurements"></div>
		<div class="no-sprite sprite_skeleton_phenotype" data-hasqtip="27"
			title="${gene.markerSymbol} skeleton phenotype measurements"></div>
		<div class="no-sprite sprite_immune_system_phenotype_or_hematopoietic_system_phenotype"
			data-hasqtip="27"
			title="${gene.markerSymbol} immune system phenotype or hematopoietic system phenotype measurements"></div>
		<div class="no-sprite sprite_muscle_phenotype" data-hasqtip="27"
			title="${gene.markerSymbol} muscle phenotype measurements"></div>
		<div class="no-sprite sprite_integument_phenotype_or_pigmentation_phenotype"
			data-hasqtip="27"
			title="${gene.markerSymbol} integument phenotype or pigmentation phenotype measurements"></div>
	
		<div class="no-sprite sprite_craniofacial_phenotype " data-hasqtip="27"
			title="${gene.markerSymbol} craniofacial phenotype measurements"></div>
		<div class="no-sprite sprite_hearing_vestibular_ear_phenotype "
			data-hasqtip="27" title="${gene.markerSymbol} hearing/vestibular/ear phenotype measurements"></div>
		<div class="no-sprite sprite_taste_olfaction_phenotype "
			data-hasqtip="27" title="${gene.markerSymbol} taste/olfaction phenotype measurements"></div>
		<div class="no-sprite sprite_endocrine_exocrine_gland_phenotype "
			data-hasqtip="27" title="${gene.markerSymbol} endocrine/exocrine gland phenotype measurements"></div>
		<div class="no-sprite sprite_vision_eye_phenotype" data-hasqtip="27"
			title="${gene.markerSymbol} vision/eye phenotype measurements"></div>
	
		<c:forEach var="group" items="${significantTopLevelMpGroups.keySet()}">
			<c:if test="${group != 'mammalian phenotype' }">
				<a href='${baseUrl}/experiments?geneAccession=${gene.mgiAccessionId}&${significantTopLevelMpGroups.get(group)}'>
					<div class="sprite_orange sprite_${group.replaceAll(' |/', '_')}" data-hasqtip="27" title="${gene.markerSymbol} ${group} measurements"></div>
				</a>
			</c:if>		
		</c:forEach>
		
		<c:forEach var="group" items="${notsignificantTopLevelMpGroups.keySet()}">
			<c:if test="${group != 'mammalian phenotype' }">
				<a href='${baseUrl}/experiments?geneAccession=${gene.mgiAccessionId}&${notsignificantTopLevelMpGroups.get(group)}'>
					<div class="sprite_blue sprite_${group.replaceAll(' |/', '_')}"	data-hasqtip="27" title="${gene.markerSymbol} ${group} measurements"></div>
				</a>
			</c:if>		
		</c:forEach>
	
	</div>
  	<div class="floatright"  style="clear: both">
  		<div class="abnormalities_key">
			<span style="color: #e27010">Significant &nbsp; &nbsp; </span>
            <span style="color: #0978a1">Not Significant &nbsp; &nbsp; </span>
            <span style="color: #c2c2c2">Not tested &nbsp; &nbsp; </span>
        </div>
	</div>
</div>
