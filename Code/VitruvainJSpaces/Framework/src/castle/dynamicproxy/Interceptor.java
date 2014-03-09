package castle.dynamicproxy;



public interface Interceptor 
{
    public boolean Intercept(Invocation invocation);
}
