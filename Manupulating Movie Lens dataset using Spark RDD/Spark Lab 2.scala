// Databricks notebook source
import sys.process._
"wget -P /tmp http://files.grouplens.org/datasets/movielens/ml-latest-small.zip" !


// COMMAND ----------

"unzip /tmp/ml-latest-small.zip -d /tmp" !

// COMMAND ----------

"ls /tmp/ml-latest-small" !

// COMMAND ----------

val ratings = sc.textFile("file:/tmp/ml-latest-small/ratings.csv")
val header = ratings.first
val data = ratings.filter(line => line != header)
val pairData = data.map(x => ((x.split(","))(1), (x.split(","))(2)))

// COMMAND ----------

val movies = sc.textFile("file:/tmp/ml-latest-small/movies.csv")
val moviesHeader = movies.first
val moviesData = movies.filter(line => line != moviesHeader)
val moviesPairData = moviesData.map(x => ((x.split(","))(0), (x.split(","))(1)))

// COMMAND ----------

val tags = sc.textFile("file:/tmp/ml-latest-small/tags.csv")
val tagsHeader = tags.first
val tagsData = tags.filter(line => line != tagsHeader)
val tagsPairData = tagsData.map(x => ((x.split(","))(1), (x.split(","))(2)))

// COMMAND ----------

//Q1. Displaying the movie that has highest count of ratings
val highestRatingsMovieId = pairData.map(x => (x._1,1)).reduceByKey((x,y) => x+y).sortBy(-_._2).take(1).head
moviesPairData.filter(x => x._1 == highestRatingsMovieId._1).collect()

// COMMAND ----------

//Q2. Displaying the movies that have lowest count of ratings
val lowestCountMovies = pairData.map(x => (x._1,1)).reduceByKey((x,y) => x+y).sortBy(_._2).filter(x => x._2 == 1)
val keyList = lowestCountMovies.keys.collect().toList
val moviesWithLowestCountRatings = moviesPairData.filter(x => keyList.contains(x._1)).collect()

// COMMAND ----------

//Q3 Displaying average rating for each movie
val sumCount = pairData.mapValues(x => (x.toDouble,1.0)).reduceByKey((x,y) => ((x._1 + y._1),(x._2 + y._2)))
val avgRatings = sumCount.mapValues{case(sum:Double,count:Double) => sum/count}
val sortedRatings = avgRatings.sortBy(-_._2)
val joined = sortedRatings.join(moviesPairData)
val sorted = joined.sortBy{case(id : String,(rating:Double,name:String)) => -rating}
val moviesAndAvgRatings = sorted.values.collect()

// COMMAND ----------

//Q4 Displaying the movies with the highest average rating
val highestAvgRating = sortedRatings.values.collect().head
val moviesWithHighestRating = moviesAndAvgRatings.filter(x => x._1 == highestAvgRating)

// COMMAND ----------

//Q5 Displaying the movies with the lowest average rating
val lowestAvgRating = sortedRatings.values.collect().last
val moviesWithLowestRating = moviesAndAvgRatings.filter(x => x._1 == lowestAvgRating)

// COMMAND ----------

//Q6  top 10 movies with the highest average ratings
moviesWithHighestRating.take(10)

// COMMAND ----------

//Q7 Displaying all movies with the tag 'mathematics'
val mathMovies = tagsPairData.filter{case(x:String,y:String) => y.contains("mathematics")}
mathMovies.join(moviesPairData).collect()

// COMMAND ----------

//Q8 Displaying average ratings of movies that contain the tag 'artificial intelligence'
val aiMovies = tagsPairData.filter{case(x:String,y:String) => y.contains("artificial intelligence")}
val aiMovieIds = aiMovies.keys.collect().toList
val aiMovieRatings = pairData.filter(x => aiMovieIds.contains(x._1))
val aiMovieCount = aiMovieRatings.count()
val aiMovieRatingSum = aiMovieRatings.map(x=> (x._1,x._2.toDouble)).reduceByKey((x,y) => (x+y))
val aiMovieAvgRating = aiMovieRatingSum.map(x => (x._1,x._2/aiMovieCount))
aiMovieAvgRating.collect()

// COMMAND ----------

//Q9 Displaying average rating of movies that have the genre 'Crime'
val moviesGenrePair =  moviesData.map(x => ((x.split(","))(0), (x.split(","))(2)))
val crimeMovies = moviesGenrePair.filter{case(x:String,y:String) => y.contains("Crime")}
val crimeMovieIds = crimeMovies.keys.collect().toList
val crimeMovieRatings = pairData.filter(x => crimeMovieIds.contains(x._1))
val crimeMovieCount = crimeMovieRatings.count()
val crimeMovieRatingSum = crimeMovieRatings.map(x=> (1,x._2.toDouble)).reduceByKey((x,y) => (x+y))
val crimeMovieTotalSum = crimeMovieRatingSum.values.collect().head
val crimeMovieAverageRating = crimeMovieTotalSum/crimeMovieCount

// COMMAND ----------

//Q10 Displaying the most popular tag 
tagsPairData.map(x => (x._2,1)).reduceByKey((x,y) => x+y).sortBy(-_._2).take(1)
