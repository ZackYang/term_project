class ProductsController < ApplicationController
  
  def index
    @products = Product.order(:id => :desc).page(params[:page]).per(50)
  end
  
  def show
    @product = Product.find(params[:id])
  end
  
end
