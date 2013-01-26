package monoxide.forgebackup.configuration;

import net.minecraftforge.common.Configuration;

enum Sections {
	GENERAL(Configuration.CATEGORY_GENERAL),
	BLOCK(Configuration.CATEGORY_BLOCK),
	ITEM(Configuration.CATEGORY_ITEM),
	BACKUP("backup"),
	LONGTERM_BACKUP("backup.longterm"),
	;
	
	private final String name;
	
	Sections(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
