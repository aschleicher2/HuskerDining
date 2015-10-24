class CreateWorkouts < ActiveRecord::Migration
  def change
    create_table :workouts do |t|
      t.string :name
      t.string :bodyPart
      t.boolean :favorite

      t.timestamps
    end
  end
end
