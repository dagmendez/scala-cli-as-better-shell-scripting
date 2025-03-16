//> using scala 3.3.5

//> using dep com.lihaoyi::os-lib:0.11.4
//> using dep com.lihaoyi::requests:0.9.0
//> using dep com.lihaoyi::upickle:4.1.0

// Create, or if it exists already, clear and recreate the output dir
val dest = os.pwd / "output"

if os.exists(dest) then os.remove.all(dest)

os.makeDir(dest)

val filename = "planets.txt"

if os.exists(os.pwd / filename) then
  // Read the file
  os.read.lines
    .stream(os.pwd / filename) // Stream in case it's a lot of data.
    .foreach: url =>
      // Request the data
      val response = requests.get(url)
      val planet   = response.text()

      // Handle failed requests
      if response.statusCode != 200
      then println(s"Error getting planet at url '$url', got '${response.statusCode}'.")
      else
        // Extract the name and handle the missing field
        ujson.read(planet)("name").strOpt match
          case None =>
            println(s"Unnamed planet at url '$url'!")

          case Some(name) =>
            // Output the data to a json file named after the planet in the output directory
            os.write.over(
              dest / s"$name.json",
              planet
            ) // Write over existing files, instead of erroring.

            // Print the name for some user feedback
            println(name)
else println(s"Could not find the expected file '$filename', in the working directory.")
