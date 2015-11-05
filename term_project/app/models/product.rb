class Product < ActiveRecord::Base

  has_many :images
  
  searchable do
    text :name, :description

    time    :created_at
  end
  
  def img
    images.first.path
  end
  
  def imgs
    images.map { |image| image.path }
  end
  
  # def self.change_path
  #   Dir.glob(Rails.root + "app/assets/images/products/product_*.*") do |path|
  #     filename = path.split('/').last
  #     unless (filename =~ /product_\d+_/)
  #       new_name = "product_#{filename.match(/\d+/).to_s}_0.jpg"
  #       File.rename(path, Rails.root + "app/assets/images/products/#{new_name}")
  #     end
  #   end
  # end
  
  def self.add_images_to_db
    all.each do |p|
      p.imgs.each do |img|
        Image.find_or_create_by(path: img, product_id: p.id)
      end
    end
  end
  
  
  
end
