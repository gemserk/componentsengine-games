builder.entity("${entityName}") {

	tags("editor")
	
	component("editor.addItem.trigger")
	component("editor.addItem")
	// component("editor.addPath")

	// property("path.targetEntity", parameters.pathTargetEntity)

	propertyRef("editor.addItem.trigger.pressed", "editor.addItem.enabled")
	propertyRef("editor.addItem.trigger.location", "editor.addItem.position")

}
