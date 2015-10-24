json.array!(@workouts) do |workout|
  json.extract! workout, :id, :name, :bodyPart, :favorite
  json.url workout_url(workout, format: :json)
end
