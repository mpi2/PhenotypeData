<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>


<div class="abnormalities">
	<div class="allicons"></div>

	<div class="no-sprite sprite_embryogenesis_phenotype" data-hasqtip="27"
		title="embryogenesis phenotype"></div>
	<div class="no-sprite sprite_reproductive_system_phenotype"
		data-hasqtip="27" title="reproductive system phenotype"></div>
	<div class="no-sprite sprite_mortality_aging" data-hasqtip="27"
		title="mortality/aging"></div>
	<div class="no-sprite sprite_growth_size_body_phenotype"
		data-hasqtip="27" title="growth/size/body phenotype"></div>
	<div class="no-sprite sprite_homeostasis_metabolism_phenotype_or_adipose_tissue_phenotype"
		data-hasqtip="27"
		title="homeostasis/metabolism phenotype or adipose tissue phenotype"></div>

	<div class="no-sprite sprite_behavior_neurological_phenotype_or_nervous_system_phenotype"
		data-hasqtip="27"
		title="behavior/neurological phenotype or nervous system phenotype"></div>
	<div class="no-sprite sprite_cardiovascular_system_phenotype"
		data-hasqtip="27" title="cardiovascular system phenotype"></div>
	<div class="no-sprite sprite_respiratory_system_phenotype"
		data-hasqtip="27" title="respiratory system phenotype"></div>
	<div class="no-sprite sprite_digestive_alimentary_phenotype_or_liver_biliary_system_phenotype"
		data-hasqtip="27"
		title="digestive/alimentary phenotype or liver/biliary system phenotype"></div>
	<div class="no-sprite sprite_renal_urinary_system_phenotype"
		data-hasqtip="27" title="renal/urinary system phenotype"></div>

	<div class="no-sprite sprite_limbs_digits_tail_phenotype"
		data-hasqtip="27" title="limbs/digits/tail phenotype"></div>
	<div class="no-sprite sprite_skeleton_phenotype" data-hasqtip="27"
		title="skeleton phenotype"></div>
	<div class="no-sprite sprite_immune_system_phenotype_or_hematopoietic_system_phenotype"
		data-hasqtip="27"
		title="immune system phenotype or hematopoietic system phenotype"></div>
	<div class="no-sprite sprite_muscle_phenotype" data-hasqtip="27"
		title="muscle phenotype"></div>
	<div class="no-sprite sprite_integument_phenotype_or_pigmentation_phenotype"
		data-hasqtip="27"
		title="integument phenotype or pigmentation phenotype"></div>

	<div class="no-sprite sprite_craniofacial_phenotype " data-hasqtip="27"
		title="craniofacial phenotype"></div>
	<div class="no-sprite sprite_hearing_vestibular_ear_phenotype "
		data-hasqtip="27" title="hearing/vestibular/ear phenotype"></div>
	<div class="no-sprite sprite_taste_olfaction_phenotype "
		data-hasqtip="27" title="taste/olfaction phenotype"></div>
	<div class="no-sprite sprite_endocrine_exocrine_gland_phenotype "
		data-hasqtip="27" title="endocrine/exocrine gland phenotype"></div>
	<div class="no-sprite sprite_vision_eye_phenotype" data-hasqtip="27"
		title="vision/eye phenotype"></div>

	<c:forEach var="group" items="${significantTopLevelMpGroups.keySet()}">
		<c:if test="${group != 'mammalian phenotype' }">
			<a href='${baseUrl}/experiments?geneAccession=${gene.mgiAccessionId}&${significantTopLevelMpGroups.get(group)}'>
				<div class="sprite_orange sprite_${group.replaceAll(' |/', '_')}" data-hasqtip="27" title="${group}"></div>
			</a>
		</c:if>		
	</c:forEach>
	
	<c:forEach var="group" items="${notsignificantTopLevelMpGroups.keySet()}">
		<c:if test="${group != 'mammalian phenotype' }">
			<a href='${baseUrl}/experiments?geneAccession=${gene.mgiAccessionId}&${notsignificantTopLevelMpGroups.get(group)}'>
				<div class="sprite_blue sprite_${group.replaceAll(' |/', '_')}"	data-hasqtip="27" title="${group}"></div>
			</a>
		</c:if>		
	</c:forEach>

</div>
