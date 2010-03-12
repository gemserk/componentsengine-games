package towerofdefense.mains;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import groovy.lang.Closure;


import groovy.lang.Closure;
import groovy.util.IndentPrinter;

public class DSLPrinter {
	
	
	StringWriter stringWriter;
	IndentPrinter out;
	
	public DSLPrinter(StringWriter stringWriter) {
		out = new IndentPrinter(new PrintWriter(stringWriter));
	}
	
	public DSLPrinter() {
		out = new IndentPrinter(new PrintWriter(new StringWriter(100)));
		
	}
	
	def methodMissing(String name, args) {
		
		def printGenericMethodCall = {name2, args2 ->
			out.printIndent()
			out.print "$name("
			out.print (args2.collect({ it.toString() }).join(","))
			out.println(")")
		}
		
		
		
		if(args.length == 2 && args[0] instanceof Map && args[1] instanceof Closure){
			out.printIndent()
			out.print "$name("
			def attributes = args[0].collect({key, value -> "$key:$value" }).join(",")
			out.println "$attributes){"
			out.incrementIndent()
			Closure closure = args[1]
			closure.delegate = this
			closure.setResolveStrategy(Closure.DELEGATE_FIRST)
			closure.call()
			out.decrementIndent()
			out.printIndent()
			out.println "}"
		} else if(args.length == 1 && args[0] instanceof Closure){
			
			out.printIndent()
			out.print "$name"
			out.println " {"
			out.incrementIndent()
			Closure closure = args[0]
			closure.delegate = this
			closure.setResolveStrategy(Closure.DELEGATE_FIRST)
			closure.call()
			out.decrementIndent()
			out.printIndent()
			out.println "}"
		}
		else if (args.length == 1 && args[0] instanceof Map){
			out.printIndent()
			out.print "$name("
			def attributes = args[0].collect({key, value -> "$key:$value" }).join(",")
			out.println "$attributes)"                     
		}
		else {
			printGenericMethodCall(name,args)
		}
		
	}
	
	public void flush(){
		out.flush();
	}
	
	public String getResultString(){
		return getResultBuffer().toString()
	}
	
	public StringBuffer getResultBuffer(){
		flush()
		return stringWriter.getBuffer()
	}
	
	
	
	public static void main(String[] args) {
		def dsl = new DSLPrinter()
		
		dsl.scene(money:30f, lives:15){
			path(minX:0,minY:30) {
				point(100, 570)		      
				point(100, 450) 		      
				point(100, 300)
				point(300, 150)		      
				point(500, 150)		      
				point(620, 300) 		      
				point(500, 500)  		      
				point(500, 500) 		      
				point(350, 450)
			}
			
			
			critters(rewardFactor:[1f], healthFactor:[1.7f]){
				critter(type:"chomper", health:70f, speed:20f)
				critter(type:"spinner",health:70f, speed:20f)
				critter(type:"wiggle", health:70f, speed:20f)
				critter(type:"star", health:70f, speed:20f)
			}
			waves(delayBetweenWaves:20000, delayBetweenSpawns:1000){
				wave(quantity:6, id:"chomper")
				wave {
					wave(quantity:2, id:"spinner")
					wave(quantity:2, id:"chomper")
					wave(quantity:2, id:"spinner")
				}
				wave(quantity:6, id:"wiggle")
				wave {
					wave(quantity:2, id:"spinner")
					wave(quantity:2, id:"star")
					wave(quantity:2, id:"spinner")
				}
				wave {
					wave(quantity:1, id:"chomper")
					wave(quantity:1, id:"wiggle")
					wave(quantity:1, id:"chomper")
					wave(quantity:1, id:"wiggle")
					wave(quantity:1, id:"chomper")
					wave(quantity:1, id:"wiggle")
				}
				wave(quantity:6, id:"star")
			}
			
			towers{
				tower(type:"blaster",cost:10f)
			}
			
		}
		
		println dsl.getResultString()
		
	}
}