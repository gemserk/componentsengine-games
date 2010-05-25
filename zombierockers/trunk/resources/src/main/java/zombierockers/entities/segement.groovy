package zombierockers.entities

import com.gemserk.games.zombierockers.PathTraversal;

builder.entity("segment-${Math.random()}") {
	
	tags("segment")
	
	property("pathTraversal", new PathTraversal(parameters.path))
	
}

