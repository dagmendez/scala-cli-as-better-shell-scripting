//> using scala 3.3.5
//> using dep com.lihaoyi::os-lib:0.11.4
//> using dep com.lihaoyi::requests:0.9.0
//> using dep com.lihaoyi::upickle:4.1.0

// Create, or if it exists already, clear and recreate the output dir
val dest = os.pwd / "output"

if os.exists(dest) then os.remove.all(dest)

os.makeDir(dest)

val filename = "better_planets.txt" // It will handle errors without crashing

if os.exists(os.pwd / filename) then
 
  // Read the lines of the input file
  os.read.lines
    .stream(os.pwd / filename) // Stream in case it's a lot of data
    .filter(_.nonEmpty) // Filter empty lines in the input file
    .foreach: url =>

      // Request the data
      val response = requests.get(url, check = false)
      val planet = response.text()

      // Handle failed requests
      if response.statusCode != 200 then 
        println(s"Error getting planet at url '$url', got '${response.statusCode}'.")
      else
        ujson.read(planet).objOpt match

          // Handle the failed reads in case the paylod is not a JsonObject
          case None => println(s"Received answer is not a JsonObject at url '$url'!")

          // Extract the name
          case Some(json) => json.get("name") match

            // Handle the missing field "name"
            case None => println(s"Unnamed planet at url '$url'!")

             // Output the data to a json file named after the planet in the output directory
            case Some(name) => 

              // Write over existing files, instead of erroring
              os.write.over(dest / s"${name.str}.json", planet)        

              // Print the name for some user feedback
              println(name.str)

else println(s"Could not find the expected file '$filename', in the working directory.")
