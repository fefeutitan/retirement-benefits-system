package br.com.beneficiosonline.integration.aws;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.aws")
public class AwsProperties {
    private boolean enabled;
    private String region = "sa-east-1";
    private String endpoint;
    private String snsTopicArn;
    private String sqsQueueUrl;

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
    public String getSnsTopicArn() { return snsTopicArn; }
    public void setSnsTopicArn(String snsTopicArn) { this.snsTopicArn = snsTopicArn; }
    public String getSqsQueueUrl() { return sqsQueueUrl; }
    public void setSqsQueueUrl(String sqsQueueUrl) { this.sqsQueueUrl = sqsQueueUrl; }
}
