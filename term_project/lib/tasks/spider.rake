require 'open-uri'
require 'net/http'
require 'nokogiri'

#A simple wrapper method that accepts either strings or URI objects
#and performs an HTTP GET.
module Net
  class HTTP
    def HTTP.get_with_headers(uri, headers=nil)
      uri = URI.parse(uri) if uri.respond_to? :to_str
      start(uri.host, uri.port) do |http|
        return http.get(uri.path, headers)
      end
    end
  end
end

def fetch_img uri
  res = Net::HTTP.get_with_headers(uri, {
    'Accept' => "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
    "Accept-Encoding" => "gzip, deflate, sdch",
    "Accept-Language" => "zh-CN,zh;q=0.8,en;q=0.6,ja;q=0.4,zh-TW;q=0.2",
    "Cache-Control" => "max-age=0",
    "Connection" => "keep-alive",
    "Host" => "img2.imgtn.bdimg.com",
    "If-Modified-Since" => "Thu, 01 Jan 1970 00:00:00 GMT",
    "If-None-Match" => "e8cb5734dddc7a2d10a546d249e8b047",
    "Referer"=>"http://www.dissh.com.au/product.aspx?id=10062&new=1&pageref=%2fshop.aspx%3fnew%3d1",
    "User-Agent" => "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.99 Safari/537.36",
    "Cookie" => "ASP.NET_SessionId=cuwbneblene20ibrk5o0p5nc; _subscriber_popup=1; cbar_uid=354866300; cbar_sess=1; _lo_no_track=1; _ga=GA1.3.353738097.1446634533; __utma=68928644.353738097.1446634533.1446634535.1446634535.1; __utmb=68928644.19.9.1446637014763; __utmc=68928644; __utmz=68928644.1446634535.1.1.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided); cbar_lvt=1446637015; cbar_sess_pv=19"
                                          
    })
  end

  namespace :spider do

    desc "Mura"
    task :murarun => :environment do
      product_urls = []
      8.times do |i|
        begin
          uri = URI("http://www.dissh.com.au/shop.aspx?page=#{i+1}&new=1")
          doc = Nokogiri::HTML(open(uri))
          doc.css(".productImg a").to_a.each do |node|
            product_urls << URI("http://www.dissh.com.au/" + node.attribute('href'))
          end
          sleep(1)
        rescue
        end
      end
      product_urls.each do |uri|
        begin
          doc = Nokogiri::HTML(open(uri))
          name = doc.css(".shopheading").inner_html
          price = doc.css(".shopprice").inner_html
          description = doc.css("#panDetails table p").to_a[2].content
          img_url = "http://www.dissh.com.au" + doc.css("#image_product_link img").attribute("src")
          product = Product.create name: name, price: price, description: description
          path = Rails.root.to_s + "/app/assets/images/product_#{product.id}.jpg"

          File.open(path, "wb") do |f|
            open(img_url, "rb") do |read_file|
              f.write(read_file.read)
            end
          end
        rescue
        end
        puts uri
        sleep(1)
      end

    end
  
    desc "Peppermayo"
    task :peppermayo => :environment do
      product_urls = []
      12.times do |i|
        begin
          uri = URI("https://www.peppermayo.com/eshop/New-Arrivals/?sort_direction=1&page=#{i+1}")
          doc = Nokogiri::HTML(open(uri))
          doc.css(".itemholder a").to_a.each do |node|
            product_urls << URI(node.attribute('href'))
          end
          sleep(1)
        rescue
        end
      end
      product_urls = product_urls.delete_if { |url| !(url.to_s =~ /https/) }.uniq
    
      product_urls.each do |uri|
        # begin
        doc = Nokogiri::HTML(open(uri))
        name = doc.css(".headingHalf h1").to_a.first.content
        price = "$" + doc.css("#product_price").to_a.first.content
        description = doc.css(".details form ul").to_a.first.content.gsub(/[\n\r\t]+/, " ")
        img_urls = doc.css(".flexslider .slides li img").to_a.map do |img_node|
          "https:" + img_node.attribute("src")
        end
        
        unless Product.find_by name: name, price: price, description: description
        
          product = Product.create name: name, price: price, description: description
          # p product.attributes
          puts img_urls
          img_urls.each_with_index do |img_url, index|
            path = Rails.root.to_s + "/app/assets/images/product_#{product.id}_#{index}.jpg"

            File.open(path, "wb") do |f|
              open(img_url, "rb") do |read_file|
                f.write(read_file.read)
              end
            end
          end #each_with_index
          
        end # unless
        
        
        # rescue
        # end
        # puts uri
        sleep(1)
      end
    end


  end
