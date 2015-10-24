class AddImageToWorkout < ActiveRecord::Migration
  def change
    add_column :workouts, :image, :string
  end
end
