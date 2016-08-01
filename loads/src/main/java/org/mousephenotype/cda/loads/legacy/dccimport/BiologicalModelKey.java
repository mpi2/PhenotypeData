package org.mousephenotype.cda.loads.legacy.dccimport;


public class BiologicalModelKey {

	String zygosity;
	String allelicComposition;
	String geneticBackground;

	public BiologicalModelKey(String zygosity, String allelicComposition, String geneticBackground) {

		this.zygosity = zygosity;
		this.allelicComposition = allelicComposition;
		this.geneticBackground = geneticBackground;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BiologicalModelKey [zygosity=" + zygosity + ", allelicComposition=" + allelicComposition + ", geneticBackground=" + geneticBackground + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((allelicComposition == null) ? 0 : allelicComposition.hashCode());
		result = prime * result + ((geneticBackground == null) ? 0 : geneticBackground.hashCode());
		result = prime * result + ((zygosity == null) ? 0 : zygosity.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		BiologicalModelKey other = (BiologicalModelKey) obj;
		if (allelicComposition == null) {
			if (other.allelicComposition != null) return false;
		} else if (!allelicComposition.equals(other.allelicComposition)) return false;
		if (geneticBackground == null) {
			if (other.geneticBackground != null) return false;
		} else if (!geneticBackground.equals(other.geneticBackground)) return false;
		if (zygosity == null) {
			if (other.zygosity != null) return false;
		} else if (!zygosity.equals(other.zygosity)) return false;
		return true;
	}


}
