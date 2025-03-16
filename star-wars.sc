//> using scala 3.3.5
//> using dep com.lihaoyi::os-lib:0.11.4
//> using dep com.lihaoyi::requests:0.9.0
//> using dep com.lihaoyi::upickle:4.1.0

import scala.util.Failure
import scala.util.Success
import scala.util.Try

// Create, or if it exists already, clear and recreate the output dir
val dest = os.pwd / "output"

if os.exists(dest) then os.remove.all(dest)

os.makeDir(dest)

val filename = "planets.txt"

if os.exists(os.pwd / filename) 
then
  // Read the file
  os.read.lines
    .stream(os.pwd / filename) // Stream in case it's a lot of data.
    .foreach: url =>

      // Request the data
      Try(requests.get(url)) match

        // Handle failed requests
        case Failure(exception) => println(s"Error getting planet at url '$url', got '${exception.getMessage()}'.")
        case Success(response) =>
          val planet = response.text()
          
          // Extract the name and handle the missing field
          Try(ujson.read(planet)("name")) match
            case Failure(exception) => println(s"Unnamed planet at url '$url'! reason: ${exception.getMessage()}")
            case Success(name) =>
            
            // Output the data to a json file named after the planet in the output directory
              os.write.over(
                dest / s"${name.str}.json",
                planet
              ) // Write over existing files, instead of erroring.
              
              // Print the name for some user feedback
              println(name.str)

else println(s"Could not find the expected file '$filename', in the working directory.")
