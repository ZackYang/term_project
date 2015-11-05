class SearchersController < ApplicationController
  
  def index
    unless params[:search]
      if params[:product_id]
        product_ids = ImageFinder.search_by_id params[:product_id], params[:by]
        @products = Product.where(id: product_ids).order(product_ids.map {|i| "ID = #{i} DESC" }.join(",")).page(params[:page]).per(15)
      end
      if params[:file_name]
        file_path = Rails.root.join('public', 'uploads', params[:file_name])
        product_ids = ImageFinder.search_by_image file_path, params[:by]
        @products = Product.where(id: product_ids).order(product_ids.map {|i| "ID = #{i} DESC" }.join(",")).page(params[:page]).per(15)
      end
    else
      search = Product.search do
        fulltext params[:search][:query]
        paginate :page => params[:page], :per_page => 15
      end
      @products = search.results
    end
    render 'products/index'
  end
  
  def create
    uploaded_io = params[:image_file]
    file_type = uploaded_io.original_filename.match(/\.\w+/).to_s
    file_name = Digest::MD5.hexdigest(Time.now.to_s) + file_type
    file_path = Rails.root.join('public', 'uploads', file_name)
    File.open(file_path, 'wb') do |file|
      file.write(uploaded_io.read)
    end
    
    product_ids = ImageFinder.search_by_image file_path, params[:by]
    @products = Product.where(id: product_ids).order(product_ids.map {|i| "ID = #{i} DESC" }.join(",")).page(params[:page]).per(15)
    params[:file_name] = file_name
    params[:authenticity_token] = nil
    params[:image_file] = nil
    
    render 'products/index'
  end
  
end
