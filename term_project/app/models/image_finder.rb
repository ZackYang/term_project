class ImageFinder
  
  def self.search_by_id product_id, type
    puts "@@"*20
    query_image_path = Rails.root.to_s + "/app/assets/images/products/product_#{product_id}_0.jpg"
    puts query_image_path
    images_results = `java -jar #{Rails.root + '..'}/946Searcher.jar #{query_image_path} -#{type} 60`
    puts "@@"*20
  
    product_ids = images_results.scan(/product_\d+_\d+/).map do |filename|
      filename.match(/\d+/).to_s.to_i
    end.uniq
  end
  
  def self.search_by_image file_path, type
    puts "@@"*20
    images_results = `java -jar #{Rails.root + '..'}/946Searcher.jar #{file_path} -#{type} 60`
    puts "@@"*20
  
    product_ids = images_results.scan(/product_\d+_\d+/).map do |filename|
      filename.match(/\d+/).to_s.to_i
    end.uniq
  end
  
end